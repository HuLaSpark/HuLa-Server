package com.hula.common.event.listener;

import com.hula.common.event.UserApprovalEvent;
import com.hula.core.user.domain.dto.RequestApprovalDto;
import com.hula.core.user.domain.vo.resp.ws.WSFriendApproval;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.impl.PushService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

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

    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = UserApprovalEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApprovalEvent event) {
        RequestApprovalDto requestApprovalDto = event.getRequestApprovalDto();
        pushService.sendPushMsg(WsAdapter.buildApprovalSend(new WSFriendApproval(requestApprovalDto.getUid())), requestApprovalDto.getTargetUid(), requestApprovalDto.getUid());
    }

}
