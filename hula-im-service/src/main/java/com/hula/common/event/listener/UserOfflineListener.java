package com.hula.common.event.listener;

import com.hula.common.event.UserOfflineEvent;
import com.hula.core.chat.service.ChatService;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.impl.PushService;
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
	private PushService pushService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserCache userCache;
    @Resource
    private WsAdapter wsAdapter;
    @Resource
    private ChatService chatService;

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
        // 改用分布式推送，服务器集群都能收到下线消息，从而推送给所有客户端
		pushService.sendPushMsg(wsAdapter.buildOfflineNotifyResp(event.getUser(), chatService.getMemberStatistic().getOnlineNum()), event.getUser().getId());
    }

}
