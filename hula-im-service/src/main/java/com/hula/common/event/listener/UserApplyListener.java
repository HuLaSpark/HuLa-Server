package com.hula.common.event.listener;

import com.hula.common.event.UserApplyEvent;
import com.hula.core.user.dao.UserApplyDao;
import com.hula.core.user.domain.entity.UserApply;
import com.hula.core.user.domain.vo.resp.ws.WSFriendApply;
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
public class UserApplyListener {
    @Resource
    private UserApplyDao userApplyDao;

    @Resource
    private PushService pushService;

    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        pushService.sendPushMsg(WsAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId(), userApply.getUid());
    }

}
