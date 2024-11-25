package com.hula.common.event.listener;

import com.hula.common.event.UserOfflineEvent;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.adapter.WSAdapter;
import com.hula.core.user.service.cache.UserCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

/**
 * 用户下线监听器
 *
 * @author nyh
 */
@Slf4j
@Component
public class UserOfflineListener {
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserCache userCache;
    @Resource
    private WSAdapter wsAdapter;

    @Async(HULA_EXECUTOR)
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDao.updateById(update);
        userCache.offline(user.getId(), user.getLastOptTime());
        // 推送给所有在线用户，该用户下线
        webSocketService.sendToAllOnline(wsAdapter.buildOfflineNotifyResp(event.getUser()), event.getUser().getId());
    }

}
