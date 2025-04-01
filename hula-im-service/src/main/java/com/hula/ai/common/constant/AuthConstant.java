package com.hula.ai.common.constant;

/**
 * 鉴权常量
 *
 * @author: 云裂痕
 * @date: 2020/11/17
 * @version: 1.0.0
 * 得其道 乾乾
 */
public interface AuthConstant {

    /**
     * 登录类型
     */
    String GRANT_TYPE = "grant_type";

    /**
     * 授权码
     */
    String AUTHORIZATION_CODE = "authorization_code";

    /**
     * 刷新token
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 认证信息Http请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";

    /**
     * 登录token
     */
    String TOKEN = "token";

    /**
     * JWT令牌前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * JWT载体key
     */
    String JWT_PAYLOAD_KEY = "payload";

    /**
     * 密码加密方式
     */
    String ENCODE = "encode";

    /**
     * 密码加密方式
     */
    String BCRYPT = "{bcrypt}";

    /**
     * 密码加密方式(不加密)
     */
    String NOOP = "{noop}";

    /**
     * jwt客户端id
     */
    String CLIENT_ID_KEY = "client_id";

    /**
     * jwt客户端id
     */
    String CLIENTID_KEY = "client-id";

    /**
     * jwt登录用户id
     */
    String USERID_KEY = "id";

    /**
     * jwt登录用户名
     */
    String USERNAME_KEY = "username";

    /**
     * jwt登录用户名
     */
    String PASSWORD_KEY = "password";

    /**
     * JWT存储权限属性
     */
    String AUTHORITY_CLAIM_NAME = "authorities";

    /**
     * JWT存储权限前缀
     */
    String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT唯一标识
     */
    String JTI = "jti";

    /**
     * JWT过期时间戳
     */
    String EXP = "exp";

}
