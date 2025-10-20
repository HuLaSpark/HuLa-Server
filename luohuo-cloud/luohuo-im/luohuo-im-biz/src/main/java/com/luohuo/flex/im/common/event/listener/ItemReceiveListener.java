package com.luohuo.flex.im.common.event.listener;

import com.luohuo.flex.im.core.user.service.cache.ItemCache;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.common.event.ItemReceiveEvent;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.entity.ItemConfig;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserBackpack;
import com.luohuo.flex.im.domain.enums.ItemTypeEnum;

import java.util.Objects;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;

/**
 * 用户收到物品监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class ItemReceiveListener {
    @Resource
    private UserDao userDao;
    @Resource
    private ItemCache itemCache;
	@Resource
	private UserCache userCache;
    @Resource
	private UserSummaryCache userSummaryCache;

    /**
     * 徽章类型，帮忙默认佩戴
     *
     * @param event 事件
     */
    @Async(LUOHUO_EXECUTOR)
    @EventListener(classes = ItemReceiveEvent.class)
    public void wear(ItemReceiveEvent event) {
        UserBackpack userBackpack = event.getUserBackpack();
        ItemConfig itemConfig = itemCache.get(userBackpack.getItemId());
        if (ItemTypeEnum.BADGE.getType().equals(itemConfig.getType())) {
            User user = userDao.getById(userBackpack.getUid());
            if (Objects.isNull(user.getItemId())) {
                userDao.wearingBadge(userBackpack.getUid(), userBackpack.getItemId());
				userCache.delete(userBackpack.getUid());
				userSummaryCache.delete(userBackpack.getUid());
            }
        }
    }

}
