package com.hula.common.event.listener;

import com.hula.common.event.UserBlackEvent;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.user.domain.enums.WSBaseResp;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.vo.resp.ws.WSBlack;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.cache.UserCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户拉黑监听器
 *
 * @author nyh
 */
@Slf4j
@Component
public class UserBlackListener {

    @Resource
    private MessageDao messageDao;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private UserCache userCache;

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void refreshRedis(UserBlackEvent event) {
        userCache.evictBlackMap();
        userCache.remove(event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void deleteMsg(UserBlackEvent event) {
        messageDao.invalidByUid(event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void sendPush(UserBlackEvent event) {
        Long uid = event.getUser().getId();
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        WSBlack black = new WSBlack(uid);
        resp.setData(black);
        resp.setType(WSRespTypeEnum.BLACK.getType());
        webSocketService.sendToAllOnline(resp, uid);
    }
}
