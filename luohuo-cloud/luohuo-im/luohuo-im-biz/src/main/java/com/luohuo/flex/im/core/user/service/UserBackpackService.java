package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.common.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author nyh
 */
public interface UserBackpackService {
    /**
     * 给用户发放物品
     * @param uid 用户id
     * @param itemId 物品id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     **/
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
