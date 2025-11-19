package com.luohuo.flex.oauth.facade;

/**
 * 验证码
 * @author tangyh
 * @version v1.0
 * @since 2024年09月20日15:41:02
 */
public interface CaptchaFacade {
    /***
     * 检查验证码是否正确
     * @param key 唯一键
     * @param code 验证码
     * @param templateCode 模版
     * @return
     */
    Boolean check(String key, String code, String templateCode);
}
