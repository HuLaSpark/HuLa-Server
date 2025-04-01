package com.hula.ai.common.constant;

/**
 * Http 常量
 *
 * @author: 云裂痕
 * @date: 2020/12/9
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface HttpConstant {

    /**
     * all
     */
    String SLASH = "/";

    /**
     * all
     */
    String ALL = "/**";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 成功
     */
    String SUCCESS = "SUCCESS";

    /**
     * 错误
     */
    String FAIL = "FAIL";

    /**
     * 成功
     */
    String OK = "OK";

    /**
     * no-cache
     */
    String NO_CACHE = "no-cache";

    /**
     * unknown
     */
    String UNKNOWN = "unknown";

    /**
     * x-forwarded-for
     */
    String X_FORWARDER_FOR = "x-forwarded-for";

    /**
     * Proxy-Client-IP
     */
    String PROXY_CLIENT_IP = "Proxy-Client-IP";

    /**
     * WL-Proxy-Client-IP
     */
    String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    /**
     * HTTP_CLIENT_IP
     */
    String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";

    /**
     * HTTP_X_FORWARDED_FOR
     */
    String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

    /**
     * api
     */
    String API = "api";

    //header
    /**
     * 系统平台 ios、android、windows、macos
     */
    String OS = "os";

    /**
     * App、wechat、tiktok-cn、alipay、baidu、H5
     */
    String PLAT_FORM = "platform";

    /**
     * 请求token
     */
    String TOKEN = "token";

    /**
     * 唯一设备号
     */
    String DEVICE = "device";

    /**
     * 应用版本号
     */
    String VERSION = "version";

    /**
     * 系统版本号
     */
    String SYSTEM_VERSION = "systemVersion";

    /**
     * 微信版本号
     */
    String WECHAT_VERSION = "wechatVersion";

    /**
     * 微信版本号
     */
    String CLIENT_ID = "client-id";

    /**
     * key
     */
    String APP_KEY = "appKey";

    /**
     * 密钥
     */
    String APP_SECRET = "appSecret";

    /**
     * 时间戳
     */
    String TIME_STAMP = "timestamp";

    /**
     * 随机数
     */
    String NONCE = "nonce";

    /**
     * 签名
     */
    String SIGN = "sign";

    /**
     * 签名忽略
     */
    String SIGN_IGNORE = "signIgnore";

}
