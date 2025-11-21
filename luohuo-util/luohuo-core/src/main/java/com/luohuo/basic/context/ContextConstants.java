package com.luohuo.basic.context;

/**
 * 跟上下文常量工具类
 *
 * @author 乾乾
 * @date 2018/12/21
 */
public final class ContextConstants {
    /**
     * 内置租户
     */
    public static final String BUILT_IN_TENANT_ID_STR = "1";
    /**
     * 请求头中携带的 应用id key
     */
    public static final String APPLICATION_ID_KEY = "ApplicationId";
    /**
     * 请求头中携带的 token key
     */
    public static final String TOKEN_KEY = "Token";
    /**
     * 请求头中携带的 客户端信息 key
     */
    public static final String AUTHORIZATION_KEY = "Authorization";
    /**
     * JWT中封装的 用户id
     */
    public static final String JWT_KEY_USER_ID = "userId";
	/**
	 * JWT中封装的 用户登录终端
	 */
	public static final String JWT_KEY_TYPE_HEADER = "typeHeader";
	/**
	 * JWT中封装的 用户登录系统
	 */
	public static final String JWT_KEY_SYSTEM_TYPE_HEADER = "systemTypeHeader";
    /**
     * JWT中封装的 子系统用户id
     */
    public static final String JWT_KEY_U_ID = "uid";
    public static final String JWT_KEY_COMPANY_ID = "CurrentCompanyId";
    public static final String JWT_KEY_TOP_COMPANY_ID = "CurrentTopCompanyId";
    public static final String JWT_KEY_DEPT_ID = "CurrentDeptId";
	public static final String JWT_KEY_DEVICE = "loginDevice";
	public static final String JWT_KEY_SYSTEM_TYPE = "systemType";

	/**
	 * Session 中存储权限列表的 key
	 */
	public static final String JWT_KEY_PERMISSION_LIST = "permissionList";

	/**
	 * Session 中存储角色列表的 key
	 */
	public static final String JWT_KEY_ROLE_LIST = "roleList";
    /**
     * JWT中封装的 随机数
     */
    public static final String JWT_KEY_UUID = "Uuid";

    /**
     * 请求头和线程变量中的 用户ID
     */
    public static final String USER_ID_HEADER = JWT_KEY_USER_ID;
	/**
	 * 请求头和线程变量中的 客户端IP
	 */
	public static final String HEADER_REQUEST_IP = "HEADER_REQUEST_IP";
	/**
	 * 请求头和线程变量中的 用户登录终端
	 */
	public static final String LOGIN_SOURCE_TYPE_HEADER = JWT_KEY_TYPE_HEADER;
	/**
	 * 请求头和线程变量中的 用户登录系统
	 */
	public static final String LOGIN_SYSTEM_TYPE_HEADER = JWT_KEY_SYSTEM_TYPE_HEADER;
    /**
     * 请求头和线程变量中的 员工ID
     */
    public static final String U_ID_HEADER = JWT_KEY_U_ID;
    /**
     * 请求头和线程变量中的 当前单位ID
     */
    public static final String CURRENT_COMPANY_ID_HEADER = JWT_KEY_COMPANY_ID;
    /**
     * 请求头和线程变量中的 当前所属的顶级公司ID
     */
    public static final String CURRENT_TOP_COMPANY_ID_HEADER = JWT_KEY_TOP_COMPANY_ID;
    /**
     * 请求头和线程变量中的 当前所属的部门ID
     */
    public static final String CURRENT_DEPT_ID_HEADER = JWT_KEY_DEPT_ID;
    /**
     * 请求头和线程变量中的 应用ID
     */
    public static final String APPLICATION_ID_HEADER = APPLICATION_ID_KEY;
    /**
     * 请求头和线程变量中的 前端页面地址栏#号后的路径
     */
    public static final String PATH_HEADER = "Path";
    /**
     * 请求头和线程变量中的 token
     */
    public static final String TOKEN_HEADER = TOKEN_KEY;
    /**
     * 请求头和线程变量中的 客户端指纹信息
     */
    public static final String CLIENT_ID = "clientId";
    /**
     * 是否boot项目
     */
    public static final String IS_BOOT = "boot";
    /**
     * 是否 内部调用项目
     */
    public static final String FEIGN = "x-feign";
    /**
     * 日志链路追踪id信息头
     */
    public static final String TRACE_ID_HEADER = "trace";
    /**
     * 灰度发布版本号
     */
    public static final String GRAY_VERSION = "gray_version";
    /**
     * WriteInterceptor 放行标志
     */
    public static final String PROCEED = "proceed";
    /**
     * WriteInterceptor 禁止执行标志
     */
    public static final String STOP = "stop";

	public static final String IGNORE_TENANT_HEADER = "x-ignore-tenant";
	public static final String HEADER_TENANT_ID = "tenantId";
    public static final String HEADER_VISIT_TENANT_ID = "visitTenantId";

    private ContextConstants() {
    }

}
