package com.hula.common.event.listener;


import cn.hutool.core.date.DateUtil;
import com.hula.common.event.UserOnlineEvent;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;
import com.hula.core.user.service.IpService;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.adapter.WSAdapter;
import com.hula.core.user.service.cache.UserCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.hula.common.config.ThreadPoolConfig.HULA_EXECUTOR;

/**
 * 用户上线监听器
 * @author nyh
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserOnlineListener {

    private WebSocketService webSocketService;
    private UserDao userDao;
    private UserCache userCache;
    private IpService ipService;
    private WSAdapter wsAdapter;

    @Async(HULA_EXECUTOR)
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        // 更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
        userCache.online(user.getId(), DateUtil.date());
        // 推送给所有在线用户，该用户上线
        webSocketService.sendToAllOnline(wsAdapter.buildOnlineNotifyResp(event.getUser()), event.getUser().getId());
    }

}
