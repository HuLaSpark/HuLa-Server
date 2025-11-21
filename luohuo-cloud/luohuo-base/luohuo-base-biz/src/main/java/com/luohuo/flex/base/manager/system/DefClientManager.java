package com.luohuo.flex.base.manager.system;

import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.flex.base.entity.system.DefClient;

/**
 * <p>
 * 通用业务接口
 * 客户端
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-13
 */
public interface DefClientManager extends SuperCacheManager<DefClient> {
    /**
     * 根据 客户端id 和 客户端秘钥查询应用
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    DefClient getClient(String clientId, String clientSecret);
}
