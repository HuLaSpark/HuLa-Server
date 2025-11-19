package com.luohuo.flex.im.common.event.listener;

import com.luohuo.flex.im.common.enums.IdempotentEnum;
import com.luohuo.flex.im.common.event.UserRegisterEvent;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.enums.ItemEnum;
import com.luohuo.flex.im.core.user.service.UserBackpackService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;

/**
 * 用户注册监听器
 * @author nyh
 */
@Slf4j
@Component
public class UserRegisterListener {
    @Resource
    private UserDao userDao;
    @Resource
    @Lazy
    private UserBackpackService userBackpackService;

    @Async(LUOHUO_EXECUTOR)
    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        //送一张改名卡
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async(LUOHUO_EXECUTOR)
    @EventListener(classes = UserRegisterEvent.class)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        // 性能瓶颈，等注册用户多了直接删掉
        int count = (int) userDao.count();
        if (count <= 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (count <= 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }

}
