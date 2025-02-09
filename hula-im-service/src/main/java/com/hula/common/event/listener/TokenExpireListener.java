package com.hula.common.event.listener;

import com.hula.common.event.TokenExpireEvent;
import com.hula.core.user.dao.UserApplyDao;
import com.hula.core.user.domain.entity.User;
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
public class TokenExpireListener {
    @Resource
    private UserApplyDao userApplyDao;

    @Resource
    private PushService pushService;

    @Async(HULA_EXECUTOR)
    @TransactionalEventListener(classes = TokenExpireEvent.class, fallbackExecution = true)
    public void forceOffline(TokenExpireEvent event) {
        User user = event.getUser();
        pushService.sendPushMsg(WsAdapter.buildInvalidateTokenResp(user), user.getId(), user.getId());
    }

}
