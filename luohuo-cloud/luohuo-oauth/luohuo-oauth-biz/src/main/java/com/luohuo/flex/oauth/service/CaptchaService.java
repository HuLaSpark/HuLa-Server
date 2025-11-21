package com.luohuo.flex.oauth.service;

import com.luohuo.basic.base.R;
import com.luohuo.flex.model.vo.query.BindEmailReq;

import java.io.IOException;
import java.util.HashMap;


/**
 * 验证码
 *
 * @author 乾乾
 * @date 2019-10-18 17:22
 */
public interface CaptchaService {

    /**
     * 生成验证码
     *
     * @param key      验证码 uuid
     * @throws IOException 异常
     */
	HashMap<String, Object> createImg(String key) throws IOException;

    /**
     * 校验验证码
     *
     * @param key          邮件、手机号、唯一字符串(图片验证码) 等
     * @param templateCode 模板
     *                     key为邮件、手机号时，templateCode = 在「运营平台」-「消息模板」-「模板标识」配置一个模板
     *                     key为唯一字符串，templateCode = CAPTCHA
     * @param value        前端上送待校验值
     * @return 是否成功
     */
    R<Boolean> checkCaptcha(String key, String templateCode, String value);

    /**
     * 发送短信验证码
     *
     * @param mobile       手机号
     * @param templateCode 模板标识
     *                     需要在「运营平台」-「消息模板」-「模板标识」配置一个短信模板
     * @return com.luohuo.basic.base.R<java.lang.Boolean>
     * @author tangyh
     * @date 2022/7/26 8:05 PM
     * @create [2022/7/26 8:05 PM ] [tangyh] [初始创建]
     */
    R<Boolean> sendSmsCode(String mobile, String templateCode);

    /**
     * 发送邮箱验证码
     *                     需要在「运营平台」-「消息模板」-「模板标识」配置一个短信模板
     * @return com.luohuo.basic.base.R<java.lang.Boolean>
     * @author tangyh
     * @date 2022/7/26 8:05 PM
     * @create [2022/7/26 8:05 PM ] [tangyh] [初始创建]
     */
    R<Long> sendEmailCode(BindEmailReq bindEmailReq);

}
