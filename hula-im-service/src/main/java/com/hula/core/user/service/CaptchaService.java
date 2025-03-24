package com.hula.core.user.service;

import java.util.HashMap;

/**
 * 验证码服务
 *
 * @author qianqian
 */
public interface CaptchaService {

    /**
	 * 发送验证码：
     * 注意验证码生成需要服务器支持，若您的验证码接口显示不出来，
     */
	HashMap<String, Object> createImg();
}
