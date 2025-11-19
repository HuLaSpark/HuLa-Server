package com.luohuo.flex.im.common.event.listener;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.common.event.UserBlackEvent;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import com.luohuo.flex.model.entity.ws.WSBlack;
import com.luohuo.flex.im.core.user.service.impl.PushService;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;

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
    private final UserSummaryCache userSummaryCache;
    private final PushService pushService;

    @Async(LUOHUO_EXECUTOR)
    @EventListener(classes = UserBlackEvent.class)
    public void refreshRedis(UserBlackEvent event) {
		userSummaryCache.evictBlackMap();
    }

    @Async(LUOHUO_EXECUTOR)
    @EventListener(classes = UserBlackEvent.class)
    public void deleteMsg(UserBlackEvent event) {
        messageDao.invalidByUid(event.getUser().getId());
    }

    @Async(LUOHUO_EXECUTOR)
    @EventListener(classes = UserBlackEvent.class)
    public void sendPush(UserBlackEvent event) {
        Long uid = event.getUser().getId();
        WsBaseResp<WSBlack> resp = new WsBaseResp<>();
        WSBlack black = new WSBlack(uid);
        resp.setData(black);
        resp.setType(WSRespTypeEnum.INVALID_USER.getType());
        pushService.sendPushMsg(resp, uid, ContextUtil.getUid());
    }
}
