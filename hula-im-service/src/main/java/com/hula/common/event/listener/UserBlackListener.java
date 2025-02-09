package com.hula.common.event.listener;

import com.hula.common.event.UserBlackEvent;
import com.hula.core.chat.dao.MessageDao;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.ws.WSBlack;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

/**
 * 用户拉黑监听器
 *
 * @author nyh
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserBlackListener {

    private final MessageDao messageDao;
    private final UserCache userCache;
    private final PushService pushService;

    @Async(HULA_EXECUTOR)
    @EventListener(classes = UserBlackEvent.class)
    public void refreshRedis(UserBlackEvent event) {
        userCache.evictBlackMap();
        userCache.remove(event.getUser().getId());
    }

    @Async(HULA_EXECUTOR)
    @EventListener(classes = UserBlackEvent.class)
    public void deleteMsg(UserBlackEvent event) {
        messageDao.invalidByUid(event.getUser().getId());
    }

    @Async(HULA_EXECUTOR)
    @EventListener(classes = UserBlackEvent.class)
    public void sendPush(UserBlackEvent event) {
        Long uid = event.getUser().getId();
        WsBaseResp<WSBlack> resp = new WsBaseResp<>();
        WSBlack black = new WSBlack(uid);
        resp.setData(black);
        resp.setType(WSRespTypeEnum.INVALID_USER.getType());
        pushService.sendPushMsg(resp, uid);
        //webSocketService.sendAll(resp, uid);
    }
}
