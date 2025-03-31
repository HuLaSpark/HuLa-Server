package com.hula.core.chat.service.impl;

import com.hula.core.chat.domain.dto.ConverseMessageDto;
import com.hula.core.chat.service.ConverseService;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.impl.PushService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通话模块
 */
@Slf4j
@Service
public class ConverseServiceImpl implements ConverseService {

	@Resource
	PushService pushService;

	@Resource
	UserCache userCache;

    @Override
    @Transactional
    public void video(Long uid, ConverseMessageDto param) {
		boolean online = userCache.isOnline(param.getToId());

		if(online) {
			pushService.sendPushMsg(WsAdapter.buildVideoMsg(param), param.getToId(), uid);
		} else {
            log.info("用户:{} 未登录", param.getToId());
        }
    }

	@Override
	public void phone(Long uid, ConverseMessageDto ConverseMessageDto) {

	}
}
