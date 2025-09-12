package com.luohuo.flex.oauth.event.listener;

import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.model.entity.base.IpInfo;
import com.luohuo.flex.model.event.UserOnlineEvent;
import com.luohuo.flex.oauth.service.IpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 用户上线监听器
 *
 * @author 乾乾
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserOnlineListener {

	private IpService ipService;
    private DefUserService defUserService;

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDb(UserOnlineEvent event) {
        Long userId = event.getUserId();
		LocalDateTime lastOptTime = event.getLastOptTime();
		IpInfo ipInfo = event.getIpInfo();

		DefUser defUser = new DefUser();
		defUser.setId(userId);
		defUser.setLastLoginTime(lastOptTime);
		defUser.setIpInfo(ipInfo);
		defUserService.updateById(defUser);

        // 更新用户ip详情
        ipService.refreshIpDetailAsync(event.getUid(), userId, ipInfo);
    }

}
