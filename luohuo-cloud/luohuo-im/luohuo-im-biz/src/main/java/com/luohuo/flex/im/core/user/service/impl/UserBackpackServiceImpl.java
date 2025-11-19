package com.luohuo.flex.im.core.user.service.impl;

import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.im.core.user.service.cache.ItemCache;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.im.common.enums.IdempotentEnum;
import com.luohuo.flex.im.common.enums.YesOrNoEnum;
import com.luohuo.flex.im.common.event.ItemReceiveEvent;
import com.luohuo.flex.im.core.user.dao.UserBackpackDao;
import com.luohuo.flex.im.domain.entity.ItemConfig;
import com.luohuo.flex.im.domain.entity.UserBackpack;
import com.luohuo.flex.im.domain.enums.ItemTypeEnum;
import com.luohuo.flex.im.core.user.service.UserBackpackService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 * @author nyh
 */
@Service
public class UserBackpackServiceImpl implements UserBackpackService {
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemCache itemCache;
    @Resource
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 组装幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    // 相同幂等如果同时发奖，需要排队等上一个执行完，取出之前数据返回
    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotent);
        // 幂等检查
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        // 业务检查
        ItemConfig itemConfig = itemCache.get(itemId);
        if (ItemTypeEnum.BADGE.getType().equals(itemConfig.getType())) {
            // 徽章类型做唯一性检查
            Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
            if (countByValidItemId > 0) {
                // 已经有徽章了不发
                return;
            }
        }
        // 发物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
		insert.setCreateBy(uid);
        userBackpackDao.save(insert);
        // 用户收到物品的事件
		SpringUtils.publishEvent(new ItemReceiveEvent(this, insert));
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
