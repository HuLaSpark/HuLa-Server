/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luohuo.flex.oauth.granter;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.exception.TokenExceedException;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.common.utils.ToolsUtil;
import com.luohuo.flex.model.entity.ws.OffLineResp;
import com.luohuo.flex.oauth.event.TokenExpireEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.luohuo.flex.oauth.vo.result.LoginResultVO;

import java.util.ArrayList;
import java.util.List;

import static com.luohuo.basic.context.ContextConstants.*;

/**
 * RefreshTokenGranter
 *
 * @author Dave Syer
 * @author zuihou
 * @date 2020年03月31日10:23:53
 */
@Component
@Slf4j
public class RefreshTokenGranter {

    @Resource
    protected SaTokenConfig saTokenConfig;

    public LoginResultVO refresh(String refreshToken) {
        // 1、验证
        Object str = SaTempUtil.parseToken(refreshToken);
		SaTempUtil.deleteToken(refreshToken);

        JSONObject obj = JSONUtil.parseObj(str);
        Long userId = obj.getLong(JWT_KEY_USER_ID);
        log.info("token={},obj={}", refreshToken, obj);
        if (userId == null) {
			throw TokenExceedException.expired();
        }

        Long topCompanyId = obj.getLong(JWT_KEY_TOP_COMPANY_ID);
        Long companyId = obj.getLong(JWT_KEY_COMPANY_ID);
        Long deptId = obj.getLong(JWT_KEY_DEPT_ID);
        Long uid = obj.getLong(JWT_KEY_U_ID);
		Long tenantId = obj.getLong(HEADER_TENANT_ID);
		String systemType = obj.getStr(JWT_KEY_SYSTEM_TYPE);
		String device = obj.getStr(JWT_KEY_DEVICE);

        // 2、处理挤下线逻辑
        String combinedDeviceType = kickout(uid, userId, systemType, device);

        // 3、为其生成新的短 token
        StpUtil.login(userId, new SaLoginModel().setDevice(combinedDeviceType));
        SaSession tokenSession = StpUtil.getTokenSession();
        tokenSession.setLoginId(userId);
		tokenSession.set(JWT_KEY_SYSTEM_TYPE, systemType);
		tokenSession.set(JWT_KEY_DEVICE, device);

        if (topCompanyId != null) {
            tokenSession.set(JWT_KEY_TOP_COMPANY_ID, topCompanyId);
        } else {
            tokenSession.delete(JWT_KEY_TOP_COMPANY_ID);
        }
        if (companyId != null) {
            tokenSession.set(JWT_KEY_COMPANY_ID, companyId);
        } else {
            tokenSession.delete(JWT_KEY_COMPANY_ID);
        }
        if (deptId != null) {
            tokenSession.set(JWT_KEY_DEPT_ID, deptId);
        } else {
            tokenSession.delete(JWT_KEY_DEPT_ID);
        }
        if (uid != null) {
            tokenSession.set(JWT_KEY_U_ID, uid);
        } else {
            tokenSession.delete(JWT_KEY_U_ID);
        }
		if (tenantId != null) {
			tokenSession.set(HEADER_TENANT_ID, tenantId);
		} else {
			tokenSession.delete(HEADER_TENANT_ID);
		}

        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setToken(tokenSession.getToken());
        resultVO.setExpire(StpUtil.getTokenTimeout());
		resultVO.setClient(device);
		resultVO.setUid(uid);
        resultVO.setRefreshToken(SaTempUtil.createToken(obj.toString(), 2 * saTokenConfig.getTimeout()));
        return resultVO;
    }

    /**
     * 处理挤下线的逻辑（刷新 token 时）
     * @param uid 登录id
     * @param userId 用户基础信息ID
     * @param systemType 系统类型
     * @param deviceType 登录设备
     * @return 组合后的设备类型
     */
    private String kickout(Long uid, Long userId, String systemType, String deviceType) {
        // 1. 组合完整的设备类型标识
        String combinedDeviceType = ToolsUtil.combineStrings(systemType, deviceType);

        // 2. 检查是否已有相同组合设备的登录
        List<String> sameDeviceTokens = new ArrayList<>();
        SaSession saSession = StpUtil.getSessionByLoginId(userId.toString(), false);

        if (ObjectUtil.isNotNull(saSession)) {
            for (SaTerminalInfo terminal : saSession.getTerminalList()) {
                String terminalDeviceType = terminal.getDeviceType();
                String tokenValue = terminal.getTokenValue();

                // 3. 匹配设备类型
                if (combinedDeviceType.equals(terminalDeviceType)) {
                    sameDeviceTokens.add(tokenValue);
                    log.info("刷新token时发现同设备登录: token={}, 设备类型={}", tokenValue, terminalDeviceType);
                }
            }
        }

        // 4. 处理已有登录
        if (CollUtil.isNotEmpty(sameDeviceTokens)) {
            for (String token : sameDeviceTokens) {
                try {
                    String clientId = StpUtil.getTokenSessionByToken(token).getString(CLIENT_ID);
                    StpUtil.kickout(token);
                    log.info("刷新token时已踢出旧会话: token={}", token);

                    SpringUtils.publishEvent(new TokenExpireEvent(this, new OffLineResp(uid, deviceType, clientId, ContextUtil.getIP(), token)));
                } catch (Exception e) {
                    log.error("刷新token时踢出旧会话失败: token={}", token, e);
                }
            }
        }
        return combinedDeviceType;
    }

}
