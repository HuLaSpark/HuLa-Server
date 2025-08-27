/*
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.luohuo.flex.oauth.granter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONObject;
import com.luohuo.basic.exception.TokenExceedException;
import com.luohuo.flex.common.utils.ToolsUtil;
import com.luohuo.flex.model.entity.ws.OffLineResp;
import com.luohuo.flex.model.event.UserOfflineEvent;
import com.luohuo.flex.oauth.emuns.LoginEnum;
import com.luohuo.flex.model.event.UserOnlineEvent;
import com.luohuo.flex.im.api.ImUserApi;
import com.luohuo.flex.oauth.event.TokenExpireEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.luohuo.basic.base.R;
import com.luohuo.basic.boot.utils.WebUtils;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.UnauthorizedException;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.StrPool;
import com.luohuo.basic.utils.TreeUtil;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.entity.user.BaseOrg;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.service.user.BaseOrgService;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.common.utils.Base64Util;
import com.luohuo.flex.model.enumeration.StateEnum;
import com.luohuo.flex.model.enumeration.base.OrgTypeEnum;
import com.luohuo.flex.model.enumeration.base.UserStatusEnum;
import com.luohuo.flex.oauth.event.LoginEvent;
import com.luohuo.flex.oauth.event.model.LoginStatusDTO;
import com.luohuo.flex.oauth.vo.param.LoginParamVO;
import com.luohuo.flex.oauth.vo.result.LoginResultVO;
import com.luohuo.flex.base.entity.system.DefClient;
import com.luohuo.flex.oauth.enumeration.system.LoginStatusEnum;
import com.luohuo.flex.base.service.system.DefClientService;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.luohuo.basic.context.ContextConstants.*;

/**
 * 验证码TokenGranter
 *
 * @author zuihou
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractTokenGranter implements TokenGranter {
    protected final SystemProperties systemProperties;
    protected final DefClientService defClientService;
    protected final DefUserService defUserService;
    protected final BaseEmployeeService baseEmployeeService;
    protected final BaseOrgService baseOrgService;
    protected final SaTokenConfig saTokenConfig;
	protected final ImUserApi imUserApi;
    @Override
    public R<LoginResultVO> login(LoginParamVO loginParam) {
        // 1. 参数校验
        R<LoginResultVO> result = checkParam(loginParam);
        if (!result.getsuccess()) {
            return result;
        }
        result = checkAuthorization();
        if (!result.getsuccess()) {
            return result;
        }

        // 2. 验证码
        result = checkCaptcha(loginParam);
        if (!result.getsuccess()) {
            return result;
        }

        // 3. 查找用户
		DefUser defUser = getUser(loginParam);

        // 5. 判断密码
        result = checkUserPassword(loginParam, defUser);
        if (!result.getsuccess()) {
            throw new BizException(result.getMsg());
        }

        // 6. 检查用户状态
        result = checkUserState(defUser);
        if (!result.getsuccess()) {
            return result;
        }

        // 7. 查询单位
        Org org = findOrg(defUser);

		// 8. 查询对应系统中的uid
		Long uid = findUid(defUser.getId(), defUser.getTenantId(), defUser.getSystemType());

		// 9. 发布登录事件
		defUser.refreshIp(ContextUtil.getIP());

        // 10. 封装token
        LoginResultVO loginResultVO = buildResult(uid, defUser, org, loginParam.getDeviceType(), loginParam.getClientId());
        LoginStatusDTO loginStatus = LoginStatusDTO.success(defUser.getId(), uid, defUser.getSystemType(), loginParam.getDeviceType());
		SpringUtils.publishEvent(new UserOnlineEvent(this, ContextUtil.getTenantId(), uid, defUser.getId(), defUser.getLastLoginTime(), defUser.getIpInfo()));
		SpringUtils.publishEvent(new LoginEvent(loginStatus));
        return R.success(loginResultVO);
    }

	/**
	 * 统一登录接口，后续增加需要登录的系统模块只需要在对应的系统新增对应的表即可，用于存储对应的系统中的头像、昵称等信息
	 * @param defUid
	 * @param systemType
	 * @return
	 */
	private Long findUid(Long defUid, Long tenantId, Integer systemType) {
		if(LoginEnum.ACCOUNT.getVal().equals(systemType)){
			return baseEmployeeService.getEmployeeByUser(defUid).getId();
		} else {
			return imUserApi.findById(defUid, tenantId).getData();
		}
	}

	/**
     * 检查参数
     *
     * @param loginParam 登录参数
     * @return com.luohuo.basic.base.R<com.luohuo.flex.oauth.vo.result.LoginResultVO>
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */
    protected abstract R<LoginResultVO> checkParam(LoginParamVO loginParam);

    /**
     * 检测客户端
     *
     * @return com.luohuo.basic.base.R<com.luohuo.flex.oauth.vo.result.LoginResultVO>
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */
    protected R<LoginResultVO> checkAuthorization() {
        String basicHeader = JakartaServletUtil.getHeader(WebUtils.request(), AUTHORIZATION_KEY, StrPool.UTF_8);
        String[] authorization = Base64Util.getAuthorization(basicHeader);
        DefClient defAuthorization = defClientService.getClient(authorization[0], authorization[1]);

        if (defAuthorization == null) {
            return R.fail("请在.env文件中配置正确的客户端ID或者客户端秘钥");
        }
        if (!defAuthorization.getState()) {
            return R.fail("客户端[%s]已被禁用", defAuthorization.getClientId());
        }
        return R.success(null);
    }


    /**
     * 检查验证码
     *
     * @param loginParam 登录参数
     * @return com.luohuo.basic.base.R<com.luohuo.flex.oauth.vo.result.LoginResultVO>
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */
    protected R<LoginResultVO> checkCaptcha(LoginParamVO loginParam) {
        return R.success(null);
    }

    /**
     * 查询用户
     *
     * @param loginParam 登录参数
     * @return com.luohuo.flex.system.entity.tenant.DefUser
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */
    protected abstract DefUser getUser(LoginParamVO loginParam);

    /**
     * 检查用户账号密码是否正确
     *
     * @param loginParam loginParam
     * @param user       user
     * @return com.luohuo.basic.base.R<com.luohuo.flex.oauth.vo.result.LoginResultVO>
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */

    protected R<LoginResultVO> checkUserPassword(LoginParamVO loginParam, DefUser user) {
        return R.success(null);
    }

    /**
     * 检查用户状态是否正常
     *
     * @param user user
     * @return com.luohuo.basic.base.R<com.luohuo.flex.oauth.vo.result.LoginResultVO>
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */
    protected R<LoginResultVO> checkUserState(DefUser user) {
        // 用户被禁用
        if (!user.getState()) {
            String msg = "您已被禁用，请联系管理员开通账号！";
            SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.fail(user.getId(), LoginStatusEnum.USER_ERROR, msg)));
            return R.fail(msg);
        }
        return R.success(null);
    }

    /**
     * 查询员工信息
     *
     * @param defUser 用户信息
     * @return com.luohuo.flex.oauth.granter.AbstractTokenGranter.Employee
     * @author tangyh
     * @date 2022/10/5 12:38 PM
     * @create [2022/10/5 12:38 PM ] [tangyh] [初始创建]
     */
    protected Employee getEmployee(DefUser defUser) {
        // 用户被禁用无法登陆， 员工被禁用无法访问当前企业的数据， 企业被禁用所有员工无法
        List<BaseEmployeeResultVO> employeeList = baseEmployeeService.listEmployeeByUserId(defUser.getId());
        Long employeeId = null;
        Long userId = defUser.getId();
        UserStatusEnum userStatus = UserStatusEnum.NORMAL;
        if (CollUtil.isNotEmpty(employeeList)) {
            BaseEmployeeResultVO defaultEmployee = employeeList.get(0);
            // 正常状态
            if (StateEnum.ENABLE.eq(defaultEmployee.getState())) {
                employeeId = defaultEmployee.getId();
            } else {
                userStatus = UserStatusEnum.USER_DISABLE;
            }
        } else {
            userStatus = UserStatusEnum.USER_DISABLE;
        }
        log.info("userStatus={}, userId={}, employeeId={}", userStatus, userId, employeeId);
        return Employee.builder().employeeId(employeeId).build();
    }

    /**
     * 查询单位和部门信息
     *
     * @param defUser 员工信息
     * @return com.luohuo.flex.oauth.granter.AbstractTokenGranter.Org
     * @author tangyh
     * @date 2022/10/5 12:40 PM
     * @create [2022/10/5 12:40 PM ] [tangyh] [初始创建]
     */
    protected Org findOrg(DefUser defUser) {
		if(defUser.getSystemType().equals(LoginEnum.IM.getVal())){
			return new Org();
		}
        Long uid = defUser.getId();

        // 当前所属部门
        Long currentDeptId = null;
        // 当前所属单位
        Long currentCompanyId = null;
        // 当前所属顶级单位
        Long currentTopCompanyId = null;
        if (uid != null) {
            BaseEmployee baseEmployee = baseEmployeeService.getByIdCache(uid);

            // 当前用户尚不属于任意租户
            if (baseEmployee == null) {
                return Org.builder()
                        .currentTopCompanyId(null)
                        .currentCompanyId(null)
                        .currentDeptId(null).build();
            }

            boolean flag = false;
            // 上次登录的部门
            if (baseEmployee.getLastDeptId() != null) {
                currentDeptId = baseEmployee.getLastDeptId();
                // TODO 若用户变更了部门，是否有问题
            } else {
                // 上次登录部门为空，则随机选择一个部门
                List<BaseOrg> deptList = baseOrgService.findDeptByEmployeeId(uid, null);
                BaseOrg defaultDept = baseOrgService.getDefaultOrg(deptList, null);

                currentDeptId = defaultDept != null ? defaultDept.getId() : null;
                baseEmployee.setLastDeptId(currentDeptId);

                flag = currentDeptId != null;
            }

            BaseOrg defaultCompany;
            if (baseEmployee.getLastCompanyId() != null) {
                currentCompanyId = baseEmployee.getLastCompanyId();

                defaultCompany = baseOrgService.getByIdCache(currentCompanyId);
            } else {
                if (currentDeptId != null) {
                    defaultCompany = baseOrgService.getCompanyByDeptId(currentDeptId);
                } else {
                    // currentDeptId 为空，员工可能直接挂在单位下、也可能挂不属于任何部门
                    List<BaseOrg> companyList = baseOrgService.findCompanyByEmployeeId(uid);
                    defaultCompany = baseOrgService.getDefaultOrg(companyList, baseEmployee.getLastCompanyId());
                }

                currentCompanyId = defaultCompany != null ? defaultCompany.getId() : null;
                baseEmployee.setLastCompanyId(currentCompanyId);
                flag = flag || currentCompanyId != null;

            }

            if (defaultCompany != null) {
                Long rootId = TreeUtil.getTopNodeId(defaultCompany.getTreePath());
                BaseOrg rootCompany;
                if (rootId != null) {
                    rootCompany = baseOrgService.getByIdCache(rootId);
                } else {
                    rootCompany = defaultCompany;
                }
                currentTopCompanyId = rootCompany != null ? rootCompany.getId() : null;
            }

            if (flag) {
                baseEmployeeService.updateById(baseEmployee);
            }
        }
        return Org.builder()
                .currentTopCompanyId(currentTopCompanyId)
                .currentCompanyId(currentCompanyId)
                .currentDeptId(currentDeptId).build();
    }

    /**
     * 构建返回值
     *
     * @param userInfo 员工信息
     * @param org      机构信息
	 * @param deviceType   登录设备
	 * @param clientId   设备指纹
     * @return com.luohuo.flex.oauth.vo.result.LoginResultVO
     * @author 乾乾
     */
    protected LoginResultVO buildResult(Long uid, DefUser userInfo, Org org, String deviceType, String clientId) {
		// 0. 处理同设备登录用户
		String combinedDeviceType = kickout(uid, userInfo, deviceType);

		// 1. 拿到登录用户的id
		String loginId = userInfo.getId().toString();
		StpUtil.login(loginId, combinedDeviceType);

		// 2. 配置登录设备、租户信息等等
        SaSession tokenSession = StpUtil.getTokenSession();
        tokenSession.setLoginId(userInfo.getId());
		tokenSession.set(JWT_KEY_SYSTEM_TYPE, userInfo.getSystemType());
		tokenSession.set(JWT_KEY_DEVICE, deviceType);
		tokenSession.set(CLIENT_HEADER, clientId);
        if (org.getCurrentTopCompanyId() != null) {
            tokenSession.set(JWT_KEY_TOP_COMPANY_ID, org.getCurrentTopCompanyId());
        } else {
            tokenSession.delete(JWT_KEY_TOP_COMPANY_ID);
        }
        if (org.getCurrentCompanyId() != null) {
            tokenSession.set(JWT_KEY_COMPANY_ID, org.getCurrentCompanyId());
        } else {
            tokenSession.delete(JWT_KEY_COMPANY_ID);
        }
        if (org.getCurrentDeptId() != null) {
            tokenSession.set(JWT_KEY_DEPT_ID, org.getCurrentDeptId());
        } else {
            tokenSession.delete(JWT_KEY_DEPT_ID);
        }
        if (userInfo.getId() != null) {
            tokenSession.set(JWT_KEY_U_ID, uid);
        } else {
            tokenSession.delete(JWT_KEY_U_ID);
        }
		if (userInfo.getTenantId() != null) {
			tokenSession.set(HEADER_TENANT_ID, userInfo.getTenantId());
		} else {
			tokenSession.delete(HEADER_TENANT_ID);
		}

        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setToken(StpUtil.getTokenValue());
		resultVO.setClient(deviceType);
        resultVO.setExpire(Long.valueOf(StpUtil.getTokenTimeout()));

        JSONObject obj = new JSONObject();
        obj.set(JWT_KEY_USER_ID, userInfo.getId());
		obj.set(JWT_KEY_U_ID, uid);
		obj.set(JWT_KEY_DEVICE, deviceType);
		obj.set(JWT_KEY_SYSTEM_TYPE, userInfo.getSystemType());
        obj.set(HEADER_TENANT_ID, userInfo.getTenantId());
		ContextUtil.setTenantId(userInfo.getTenantId());

		// 后台系统才需要
		if(!userInfo.getSystemType().equals(LoginEnum.IM.getVal())){
			obj.set(JWT_KEY_TOP_COMPANY_ID, tokenSession.get(JWT_KEY_TOP_COMPANY_ID));
			obj.set(JWT_KEY_COMPANY_ID, tokenSession.get(JWT_KEY_COMPANY_ID));
			obj.set(JWT_KEY_DEPT_ID, tokenSession.get(JWT_KEY_DEPT_ID));
		}

        resultVO.setRefreshToken(SaTempUtil.createToken(obj.toString(), 2 * saTokenConfig.getTimeout()));

        log.info("用户：{} 登录成功", userInfo.getUsername());
        return resultVO;
    }

	/**
	 * 处理挤下线的逻辑
	 * @param uid 登录id
	 * @param userInfo 用户基础信息
	 * @param deviceType 登录设备
	 * @return
	 */
	@Nullable
	private String kickout(Long uid, DefUser userInfo, String deviceType) {
		// 1. 组合完整的设备类型标识
		String combinedDeviceType = ToolsUtil.combineStrings(userInfo.getSystemType().toString(), deviceType);

		// 3. 检查是否已有相同组合设备的登录
		List<String> sameDeviceTokens = new ArrayList<>();
		SaSession saSession = StpUtil.getSessionByLoginId(userInfo.getId().toString(), false);

		if(ObjectUtil.isNotNull(saSession)){
			for (SaTerminalInfo terminal : saSession.getTerminalList()) {
				String terminalDeviceType = terminal.getDeviceType();
				String tokenValue = terminal.getTokenValue();

				// 4. 匹配设备类型
				if (combinedDeviceType.equals(terminalDeviceType)) {
					sameDeviceTokens.add(tokenValue);
					log.info("发现同设备登录: token={}, 设备类型={}", tokenValue, terminalDeviceType);
				}
			}
		}

		// 3. 处理已有登录
		if (CollUtil.isNotEmpty(sameDeviceTokens)) {
			for (String token : sameDeviceTokens) {
				try {
					String clientId = StpUtil.getTokenSessionByToken(token).getString(CLIENT_HEADER);
					StpUtil.kickout(token);
					log.info("已踢出会话: token={}", token);

					SpringUtils.publishEvent(new TokenExpireEvent(this, new OffLineResp(uid, deviceType, clientId, ContextUtil.getIP(), token)));
				} catch (Exception e) {
					log.error("踢出会话失败: token={}", token, e);
				}
			}
		}
		return combinedDeviceType;
	}

	/**
	 * 退出登录时 临时token还存在
	 * @return
	 */
    @Override
    public R<Boolean> logout() {
		try {
			String tokenValue = StpUtil.getTokenValue();
			String deviceType = StpUtil.getLoginDevice();
			Object loginId = StpUtil.getLoginId();
			SaSession saSession = StpUtil.getTokenSession();
			long uid = saSession.getLong(JWT_KEY_U_ID);
			Long tenantId = saSession.getLong(HEADER_TENANT_ID);

			// 1. 注销当前会话
			StpUtil.logout();

			// 2. 物理删除 Token 和 Session
			SaManager.getSaTokenDao().delete(tokenValue);
			SaManager.getSaTokenDao().deleteSession(tokenValue);

			// 3. 清理同设备类型的其他 Token（确保互斥）
			List<String> tokens = StpUtil.getTokenValueListByLoginId(loginId);
			tokens.forEach(token -> {
				if (deviceType.equals(StpUtil.getTokenSessionByToken(token).get(JWT_KEY_DEVICE))) {
					StpUtil.kickoutByTokenValue(token);
				}
			});

			// 5. 检查是否全部设备已退出
			if(StpUtil.getTokenValueListByLoginId(loginId).isEmpty()){
				SpringUtils.publishEvent(new UserOfflineEvent(this, tenantId, Long.parseLong(loginId.toString()), uid, LocalDateTime.now()));
			}
		} catch (Exception e) {
			log.debug("Token已失效，无需清理");
			throw TokenExceedException.expired();
		}
        return R.success(true);
    }

    @Override
    public LoginResultVO switchOrg(Long orgId, String clientId) {
        StpUtil.checkLogin();
        Long userId = ContextUtil.getUserId();
        DefUser defUser = defUserService.getByIdCache(userId);
        if (defUser == null) {
            throw UnauthorizedException.wrap(ResponseEnum.JWT_TOKEN_EXPIRED);
        }

        if (!Convert.toBool(defUser.getState(), Boolean.valueOf(true))) {
            throw UnauthorizedException.wrap(ResponseEnum.JWT_USER_DISABLE);
        }

        BaseEmployee employee = baseEmployeeService.getEmployeeByUser(userId);
        ArgumentAssert.notNull(employee, "您不属于该公司，无法切换");
        if (!Convert.toBool(employee.getState(), Boolean.valueOf(true))) {
            throw BizException.wrap(ResponseEnum.JWT_EMPLOYEE_DISABLE);
        }

		Long topCompanyId = null;
        Long companyId = null;
        Long deptId = null;
        if (orgId != null) {
            BaseOrg selectOrg = baseOrgService.getByIdCache(orgId);
            ArgumentAssert.notNull(selectOrg, "该部门不存在");

            if (OrgTypeEnum.COMPANY.eq(selectOrg.getType())) {
                companyId = selectOrg.getId();

                Long rootId = TreeUtil.getTopNodeId(selectOrg.getTreePath());
                if (rootId != null) {
                    BaseOrg rootCompany = baseOrgService.getByIdCache(rootId);
                    topCompanyId = rootCompany != null ? rootCompany.getId() : companyId;
                } else {
                    topCompanyId = companyId;
                }
            } else {
                deptId = selectOrg.getId();

                BaseOrg company = baseOrgService.getCompanyByDeptId(deptId);
                if (company != null) {
                    companyId = company.getId();

                    Long rootId = TreeUtil.getTopNodeId(company.getTreePath());
                    if (rootId != null) {
                        BaseOrg rootCompany = baseOrgService.getByIdCache(rootId);
                        topCompanyId = rootCompany != null ? rootCompany.getId() : companyId;
                    } else {
                        topCompanyId = companyId;
                    }
                }
            }

            baseEmployeeService.updateOrgInfo(employee.getId(), companyId, deptId);
        } else {
            baseEmployeeService.updateOrgInfo(employee.getId(), companyId, deptId);
        }

        Org org = Org.builder()
                .currentTopCompanyId(topCompanyId)
                .currentCompanyId(companyId)
                .currentDeptId(deptId)
                .build();

        LoginResultVO loginResultVO = buildResult(ContextUtil.getUid(), defUser, org, StpUtil.getLoginType(), clientId);

        LoginStatusDTO loginStatus = LoginStatusDTO.switchOrg(defUser.getId(), employee.getId());
        SpringUtils.publishEvent(new LoginEvent(loginStatus));
        return loginResultVO;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    private static class Employee {
        private Long employeeId;
    }

    @Builder
    @AllArgsConstructor
    @Getter
	@NoArgsConstructor
    private static class Org {
        private Long currentCompanyId;
        private Long currentTopCompanyId;
        private Long currentDeptId;
    }

}
