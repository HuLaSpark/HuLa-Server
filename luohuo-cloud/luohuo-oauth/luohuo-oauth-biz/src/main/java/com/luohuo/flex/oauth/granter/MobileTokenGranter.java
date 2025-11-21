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
import com.luohuo.flex.base.service.system.DefClientService;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.service.user.BaseOrgService;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.im.api.ImUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import com.luohuo.basic.base.R;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.StrHelper;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.model.enumeration.base.MsgTemplateCodeEnum;
import com.luohuo.flex.oauth.event.LoginEvent;
import com.luohuo.flex.oauth.event.model.LoginStatusDTO;
import com.luohuo.flex.oauth.service.CaptchaService;
import com.luohuo.flex.oauth.vo.param.LoginParamVO;
import com.luohuo.flex.oauth.vo.result.LoginResultVO;
import com.luohuo.flex.oauth.enumeration.system.LoginStatusEnum;

import static com.luohuo.flex.oauth.granter.MobileTokenGranter.GRANT_TYPE;


/**
 * 手机号登录获取token
 *
 * @author Dave Syer
 * @author 乾乾
 * @date 2020年03月31日10:22:55
 */
@Component(GRANT_TYPE)
public class MobileTokenGranter extends AbstractTokenGranter implements TokenGranter {

    public static final String GRANT_TYPE = "MOBILE";
	@Resource
    private CaptchaService captchaService;

	public MobileTokenGranter(SystemProperties systemProperties, DefClientService defClientService, DefUserService defUserService, BaseEmployeeService baseEmployeeService, BaseOrgService baseOrgService, SaTokenConfig saTokenConfig, ImUserApi userApi, com.luohuo.flex.oauth.biz.StpInterfaceBiz stpInterfaceBiz) {
		super(systemProperties, defClientService, defUserService, baseEmployeeService, baseOrgService, saTokenConfig, userApi, stpInterfaceBiz);
	}

	@Override
    public R<LoginResultVO> checkParam(LoginParamVO loginParam) {
        String mobile = loginParam.getMobile();
        String code = loginParam.getCode();
        if (StrHelper.isAnyBlank(mobile, code)) {
            return R.fail("请输入手机号或验证码");
        }

        return R.success(null);
    }

    @Override
    protected R<LoginResultVO> checkCaptcha(LoginParamVO loginParam) {
        if (systemProperties.getVerifyCaptcha()) {
            R<Boolean> check = captchaService.checkCaptcha(loginParam.getMobile(), MsgTemplateCodeEnum.MOBILE_LOGIN.getCode(), loginParam.getCode());
            if (!check.getsuccess()) {
                String msg = check.getMsg();
                SpringUtils.publishEvent(new LoginEvent(LoginStatusDTO.smsCodeError(loginParam.getMobile(), LoginStatusEnum.SMS_CODE_ERROR, msg)));
                throw BizException.validFail(check.getMsg());
            }
        }
        return R.success(null);
    }

    @Override
    protected DefUser getUser(LoginParamVO loginParam) {
		return defUserService.getUserByMobile(loginParam.getSystemType(), loginParam.getMobile());
    }

}
