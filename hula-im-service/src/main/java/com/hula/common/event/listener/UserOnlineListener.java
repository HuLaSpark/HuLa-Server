package com.hula.common.event.listener;

import cn.hutool.core.date.DateUtil;
import com.hula.common.event.UserOnlineEvent;
import com.hula.core.chat.service.ChatService;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.service.IpService;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.cache.UserInfoCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

/**
 * 用户上线监听器
 *
 * @author nyh
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserOnlineListener {

    private WebSocketService webSocketService;
    private UserDao userDao;
    private UserCache userCache;
	private UserInfoCache userInfoCache;
    private IpService ipService;
    private WsAdapter wsAdapter;
    private ChatService chatService;

    @Async(HULA_EXECUTOR)
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDb(UserOnlineEvent event) {
        User user = event.getUser();
        User update = User.builder().id(user.getId())
                .lastOptTime(user.getLastOptTime())
                .ipInfo(user.getIpInfo())
                .activeStatus(ChatActiveStatusEnum.ONLINE.getStatus()).build();
        userDao.updateById(update);
        // 更新用户ip详情
        ipService.refreshIpDetailAsync(user);
		userCache.delUserInfo(user.getId());
		userInfoCache.delete(user.getId());
        userCache.online(user.getId(), DateUtil.date());
        // 推送给所有在线用户，该用户上线
        webSocketService.sendAll(wsAdapter.buildOnlineNotifyResp(user, chatService.getMemberStatistic().getOnlineNum()), event.getUser().getId());
    }

}
