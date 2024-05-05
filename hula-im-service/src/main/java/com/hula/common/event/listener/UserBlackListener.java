package com.hula.common.event.listener;

import com.hula.common.event.UserBlackEvent;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.websocket.domain.service.WebSocketService;
import com.hula.core.websocket.domain.service.adapter.WebSocketAdapter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户拉黑监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserBlackListener {

    @Resource
    private UserDao userDao;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private UserCache userCache;

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void sendMsg(UserBlackEvent event) {
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void changeUserStatus(UserBlackEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void evictCache(UserBlackEvent event) {
        userCache.evictBlackMap();
    }
}
