package com.luohuo.flex.im.common.event.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import com.luohuo.flex.im.common.event.UserApprovalEvent;
import com.luohuo.flex.im.domain.dto.RequestApprovalDto;
import com.luohuo.flex.model.entity.ws.WSFriendApproval;
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
public class UserApprovalListener {
    @Resource
    private PushService pushService;

    @Async(LUOHUO_EXECUTOR)
    @TransactionalEventListener(classes = UserApprovalEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApprovalEvent event) {
        RequestApprovalDto requestApprovalDto = event.getRequestApprovalDto();
        pushService.sendPushMsg(WsAdapter.buildApprovalSend(new WSFriendApproval(requestApprovalDto.getUid())), requestApprovalDto.getTargetUid(), requestApprovalDto.getUid());
    }

}
