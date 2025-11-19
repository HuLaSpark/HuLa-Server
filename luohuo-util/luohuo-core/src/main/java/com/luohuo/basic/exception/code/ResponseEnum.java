package com.luohuo.basic.exception.code;


/**
 * 异常编码
 * <p>
 * 1**	信息，服务器收到请求，需要请求者继续执行操作
 * 2**	成功，操作被成功接收并处理
 * 3**	重定向，需要进一步的操作以完成请求
 * 4**	客户端错误，请求包含语法错误或无法完成请求
 * 5**	服务器错误，服务器在处理请求的过程中发生了错误
 * <p>
 * 业务编码规则：
 * [系统]_[模块]_[功能]： 每段3位数，_只是java或js语言的语法糖，实际上的数字编码并不存在下划线
 *
 * @author 乾乾
 * @date 2017-12-13 16:22
 */
public enum ResponseEnum implements BaseExceptionCode {

    //系统相关 start
    SUCCESS(200, "成功"),
    SYSTEM_BUSY(-1, "系统繁忙~请稍后再试~"),
    SYSTEM_TIMEOUT(-2, "系统维护中~请稍后再试~"),
    PARAM_EX(-3, "参数类型解析异常"),
    SQL_EX(-4, "运行SQL出现异常"),
    NULL_POINT_EX(-5, "服务器异常"),
    ILLEGAL_ARGUMENT_EX(-6, "无效参数异常"),
    MEDIA_TYPE_EX(-7, "请求类型异常"),
    LOAD_RESOURCES_ERROR(-8, "加载资源出错"),
    BASE_VALID_PARAM(-9, "统一验证参数异常"),
    OPERATION_EX(-10, "操作异常"),
    SERVICE_MAPPER_ERROR(-11, "Mapper类转换异常"),
    CAPTCHA_ERROR(-12, "验证码校验失败"),
    JSON_PARSE_ERROR(-13, "JSON解析异常"),


    OK(200, "OK"),
    BAD_REQUEST(400, "错误的请求"),
    /**
     * 登录未授权。 客户端在访问请求的资源之前，对token进行验证，token无法正常解析时，返回此错误码，可以简单的理解为没有登录此站。
     * <p>
     * {@code 401 Unauthorized}. 需要重新登录。
     * 该HTTP状态码表示认证错误，它是为了认证设计的，而不是为了授权设计的。收到401响应，表示请求没有被认证—压根没有认证或者认证不正确—但是请重新认证和重试。（一般在响应头部包含一个WWW-Authenticate来描述如何认证）。通常由web服务器返回，而不是web应用。从性质上来说是临时的东西。（服务器要求客户端重试）
     *
     * @see <a href="http://tools.ietf.org/html/rfc7235#section-3.1">HTTP/1.1: Authentication, section 3.1</a>
     */
    UNAUTHORIZED(401, "解析用户身份错误，请重新登录！"),
    /**
     * 访问资源 被禁止。 资源不可用，服务器理解客户的请求，但拒绝处理它。通常由于服务器上文件或目录的权限设置导致，可以简单的理解为没有权限访问此站。
     * 该HTTP状态码是关于授权方面的。从性质上来说是永久的东西，和应用的业务逻辑相关联。它比401更具体，更实际。收到403响应表示服务器完成认证过程，但是客户端请求没有权限去访问要求的资源。
     */
    FORBIDDEN(403, "禁止访问"),
    /**
     * {@code 404 Not Found}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.4">HTTP/1.1: Semantics and Content, section 6.5.4</a>
     */
    NOT_FOUND(404, "没有找到资源"),
	JWT_TOKEN_EXCEED(406, "token已过期"),
    METHOD_NOT_ALLOWED(405, "不支持当前请求类型"),

    TOO_MANY_REQUESTS(429, "请求超过次数限制"),
    INTERNAL_SERVER_ERROR(500, "内部服务错误"),
    BAD_GATEWAY(502, "网关错误"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    //系统相关 end


    JWT_USER_DISABLE(100_000_001, "您的账号被禁用，请联系平台管理员"),
    JWT_EMPLOYEE_DISABLE(100_000_002, "您在该公司的账号被禁用，请联系公司管理员"),
    JWT_TENANT_DISABLE(100_000_003, "您所在的公司已被禁用，请联系公司管理员"),
    JWT_APPLICATION_FORBIDDEN(100_000_004, "您所在的尚未开通该系统使用权限，现在为您返回基础平台！"),
    JWT_RESOURCE_FORBIDDEN(100_000_005, "您所在的企业尚未开通该资源使用权限，请联系公司管理员开通！"),

    TRANSACTIONAL(100_100_001, "跨库处理数据失败"),

    REQUIRED_FILE_PARAM_EX(1001, "请求中必须至少包含一个有效文件"),
    DATA_SAVE_ERROR(2000, "新增数据失败"),
    DATA_UPDATE_ERROR(2001, "修改数据失败"),
    TOO_MUCH_DATA_ERROR(2002, "批量新增数据过多"),
    //jwt token 相关 start

    JWT_BASIC_INVALID(40000, "无效的基本身份验证令牌"),
    JWT_TOKEN_EXPIRED(40001, "会话超时，请重新登录"),
    JWT_SIGNATURE(40002, "不合法的token，请认真比对 token 的签名"),
    JWT_ILLEGAL_ARGUMENT(40003, "缺少token参数"),
    JWT_USER_INVALID(40006, "用户名或密码错误"),
    JWT_USER_ENABLED(40007, "用户已经被禁用！"),
    JWT_OFFLINE(40008, "您已在另一个设备登录！"),
    JWT_NOT_LOGIN(40009, "登录超时，请重新登录！"),
    //jwt token 相关 end


    ;

    private final int code;
    private String msg;

    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }


    public ResponseEnum build(String msg, Object... param) {
        this.msg = String.format(msg, param);
        return this;
    }

    public ResponseEnum param(Object... param) {
        msg = String.format(msg, param);
        return this;
    }
}
