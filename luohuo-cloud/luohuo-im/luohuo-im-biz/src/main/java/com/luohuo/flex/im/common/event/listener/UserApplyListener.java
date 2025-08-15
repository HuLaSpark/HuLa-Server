package com.luohuo.flex.im.common.event.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import com.luohuo.flex.im.common.event.UserApplyEvent;
import com.luohuo.flex.im.core.user.dao.UserApplyDao;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.model.entity.ws.WSFriendApply;
import com.luohuo.flex.im.core.user.service.adapter.WsAdapter;
import com.luohuo.flex.im.core.user.service.impl.PushService;

import static com.luohuo.flex.im.common.config.ThreadPoolConfig.LUOHUO_EXECUTOR;

/**
 * 好友申请监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserApplyListener {
    @Resource
    private UserApplyDao userApplyDao;

    @Resource
    private PushService pushService;

    @Async(LUOHUO_EXECUTOR)
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        pushService.sendPushMsg(WsAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId(), userApply.getUid());
    }

}
