package com.luohuo.flex.model.constant;

/**
 * Echo 注解中api的常量
 * <p>
 * 切记，该类下的接口和方法，一定要自己手动创建，否则会注入失败
 * <p>
 * 本类中的 @luohuo.generator auto insert 请勿删除
 *
 * @author zuihou
 * @date 2020年01月20日11:16:37
 */
public interface EchoApi {
    // @luohuo.generator auto insert EchoApi

    /**
     * 字典 回显实现类
     */
    String DICTIONARY_ITEM_FEIGN_CLASS = "dictFacadeImpl";
    /**
     * 组织 回显实现类
     */
    String ORG_ID_CLASS = "orgFacadeImpl";
    /**
     * 岗位 回显实现类
     */
    String POSITION_ID_CLASS = "positionFacadeImpl";
    /** 用户 回显实现类 */
    String DEF_USER_ID_CLASS = "defUserFacadeImpl";
	/** IM用户 回显实现类 */
	String IM_USER_ID_CLASS = "ImUserFacadeImpl";

    String DEF_TENANT_SERVICE_IMPL_CLASS = "defTenantManagerImpl";
    String DEF_APPLICATION_SERVICE_IMPL_CLASS = "defApplicationManagerImpl";

}
