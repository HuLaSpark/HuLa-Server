package com.luohuo.flex.common.cache;

/**
 * 用于同一管理和生成缓存的key， 避免多个项目使用的key重复
 * <p>
 * 使用@Cacheable时， 其value值一定要在此处指定
 *
 * @author 乾乾
 * @date 2020/10/21
 */
public interface CacheKeyTable {

    /**
     * 验证码 前缀
     * 完整key: captcha:{key} -> str
     */
    String CAPTCHA = "captcha";
    /**
     * token 前缀
     * 完整key： token:{token} -> userid
     */
    String TOKEN = "token";


    //------------------

    // 权限系统缓存 start
    /**
     * 总 登录次数
     * total_login_pv:{TENANT} -> Long
     */
    String TOTAL_LOGIN_PV = "total_login_pv";
    /**
     * 今日 登录次数
     * today_login_pv:{TENANT}:{today} -> Long
     */
    String TODAY_LOGIN_PV = "today_login_pv";
    /**
     * 今日登录总ip
     * today_login_iv:{TENANT}:{today} -> int
     */
    String TODAY_LOGIN_IV = "today_login_iv";
    /**
     * 今日登录总ip
     * TOTAL_LOGIN_IV:{TENANT} -> int
     */
    String TOTAL_LOGIN_IV = "total_login_iv";
    /**
     * 今日 PV
     * today_pv:{TENANT} -> int
     */
    String TODAY_PV = "today_pv";
    /**
     * 总 PV
     * total_pv:{TENANT} -> int
     */
    String TOTAL_PV = "total_pv";
    /**
     * 最近10访问记录
     * login_log_tenday:{TENANT}:{today}:{account} -> Map
     */
    String LOGIN_LOG_TEN_DAY = "login_log_tenday";
    /**
     * 登录总次数
     * login_log_browser:{TENANT} -> Map
     */
    String LOGIN_LOG_BROWSER = "login_log_browser";
    /**
     * 登录总次数
     * login_log_system{TENANT} -> Map
     */
    String LOGIN_LOG_SYSTEM = "login_log_system";
    /**
     * 参数 前缀
     * 完整key: parameter_key:{key} -> obj
     */
    String PARAMETER_KEY = "parameter_key";
    /**
     * 在用用户 前缀
     * 完整key: online:{userid} -> token (String)
     */
    String ONLINE = "online";
    /**
     * 用户token 前缀
     * 完整key: token_user_id:{token} -> userid (Long)
     */
    String TOKEN_USER_ID = "token_user_id";
    /**
     * 用户注册 前缀
     * 完整key: register:{注册类型}:{手机号}
     */
    String REGISTER_USER = "register";

	interface Friend {
		/**
		 * 好友的关联映射
		 */
		String RELATION = "friend_relation";
		/**
		 * 好友的反向映射
		 */
		String REVERSE_FRIENDS = "reverse_friends";
		/**
		 * 好友关系
		 */
		String USER_FRIENDS = "user_friends";
		/**
		 * 好友关联的状态映射
		 */
		String RELATION_STATUS = "relation_status";
	}

	interface Presence {
		/**
		 * 全局用户
		 */
		String GLOBAL_USERS_ONLINE = "global_users_online";

		/**
		 * 全局用户映射
		 */
		String GLOBAL_DEVICES_ONLINE = "global_devices_online";

		/**
		 * 用户有多少个群的映射
		 */
		String USERS_GROUP = "users_group";

		/**
		 * 用户有多少个群中在线的映射
		 */
		String USERS_GROUP_ONLINE = "users_group_online";

		/**
		 * 群组成员构建器
		 */
		String GROUP_MEMBERS = "group_members";

		/**
		 * 群组在线成员构建器
		 */
		String GROUP_MEMBERS_ONLINE = "group_members_online";
	}

	interface VideoCall  {
		/**
		 * 管理员的元数据
		 */
		String META_DATA_ADMIN = "meta_data_admin";
	}

    interface System {

		/**
		 * 系统缓存
		 */
		String SYS_CONFIG = "sys_config";

        /**
         * 租户
         */
        String TENANT = "def_tenant";
        /**
         * 应用
         */
        String APPLICATION = "def_application";
        /**
         * 默认字典
         */
        String DICT = "def_dict";
        /**
         * 默认参数
         */
        String DEF_PARAMETER = "def_parameter";

        /**
         * 用户 前缀
         */
        String DEF_USER = "def_user";
        /**
         * 客户端
         */
        String DEF_CLIENT = "def_client";

        /**
         * 租户拥有的资源
         */
        String TENANT_APPLICATION_RESOURCE = "t_a_r";
        /**
         * 租户拥有的应用
         */
        String TENANT_APPLICATION = "t_a";

        /**
         * 资源
         */
        String RESOURCE = "dr";
        /**
         * 资源接口
         */
        String RESOURCE_API = "dra";
        /** 应用的资源 */
        String APPLICATION_RESOURCE = "app_res";
        String ALL_RESOURCE_API = "all_dra";
    }

	interface Chat{
		/**
		 * 房间元数据
		 */
		String ROOM_META = "room_meta";

		/**
		 * 关闭房间
		 */
		String CLOSE_ROOM = "close_room";

		/**
		 * 密码
		 */
		String CHAT_PASSWORD = "chat_password";
		/**
		 * chat-gpt
		 */
		String CHAT_GPT = "chat_GPT";
		/**
		 * 认证
		 */
		String AUTH = "auth";
		/**
		 * 异步
		 */
		String AYSNC = "aysnc";
		/**
		 * 聊天token
		 */
		String CHAT_TOKEN = "chat_token";
		/**
		 * 用户
		 */
		String USER_CACHE = "user_cache";
		/**
		 * 微信消息
		 */
		String WX_MSG = "wxMsg";
		/**
		 * 用户在线状态
		 */
		String USER_STATE = "user_state";
		/**
		 * 用户在线状态
		 */
		String ANNOUNCEMENTS = "announcements";
		/**
		 * 朋友圈
		 */
		String FEED = "feed";
		/**
		 * 朋友圈素材
		 */
		String FEED_MEDIA = "feedMedia";
		/**
		 * 朋友圈权限
		 */
		String FEED_TARGET = "feedTarget";

		/**
		 * 朋友圈评论
		 */
		String FEED_COMMENT = "feedComment";

		/**
		 * 朋友圈点赞
		 */
		String FEED_LIKE = "feedLike";

		/**
		 * 会话信息
		 */
		String USER_CONTACT = "user_contact";

		/**
		 * 在途消息
		 */
		String PASSAGE_MSG = "passage_msg";
	}

	interface OAUTH {

		/**
		 * 租户自定义字典
		 */
		String QR = "qr_status";
	}

    /**
     * 消息服务缓存 start
     */
    interface Base {

        /**
         * 租户自定义字典
         */
        String BASE_DICT = "base_dict";
        /**
         * 组织 前缀
         */
        String BASE_ORG = "base_org";
        /**
         * 岗位 前缀
         */
        String BASE_POSITION = "base_position";
        /**
         * 员工 前缀
         */
        String BASE_EMPLOYEE = "base_employee";
        /**
         * 全局员工 前缀
         */
        String DEF_USER_TENANT = "def_user_tenant";

        /**
         * 角色 前缀
         * 完整key: role:{roleId}
         */
        String ROLE = "role";
        /**
         * 角色拥有那些资源 前缀
         * 完整key: role_resource:{ROLE_ID} -> [RESOURCE_ID, ...]
         */
        String ROLE_RESOURCE = "role_resource";
        /**
         * 员工拥有那些角色 前缀
         * 完整key: employee_role:{EMPLOYEE_ID} -> [ROLE_ID, ...]
         */
        String EMPLOYEE_ROLE = "employee_role";

        /**
         * 角色拥有那些组织 前缀
         * 完整key: employee_org:{EMPLOYEE_ID} -> [ORG_ID, ...]
         */
        String EMPLOYEE_ORG = "employee_org";

        /**
         * 角色拥有那些组织 前缀
         * 完整key: org_role:{ORG_ID} -> [ROLE_ID, ...]
         */
        String ORG_ROLE = "org_role";
    }
    // 消息服务缓存 end


}
