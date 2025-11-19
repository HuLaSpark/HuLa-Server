package com.luohuo.flex.im.core.user.service.impl;

import com.luohuo.flex.im.core.user.dao.UserStateDao;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.domain.entity.UserState;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserStateVo;
import com.luohuo.flex.im.core.user.service.UserService;
import com.luohuo.flex.im.core.user.service.UserStateService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 乾乾
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserStateServiceImpl implements UserStateService {

    private UserStateDao userStateDao;
	private UserCache userCache;
	private UserSummaryCache userSummaryCache;
	private UserService userService;
	private PushService pushService;

	@Override
	public List<UserState> list() {
		return userStateDao.list();
	}

	@Override
	public Boolean changeState(Long uid, Long userStateId) {
		UserInfoResp userInfo = userService.getUserInfo(uid);
		// 如果检测到状态没有改变，那么则不动
		if (userInfo.getUserStateId().equals(userStateId)){
			return true;
		}

		UserState userState = userStateDao.getById(userStateId);
		userService.changeUserState(uid, userStateId);

		// 1.清除缓存
		userCache.delete(uid);
		userSummaryCache.delete(uid);

		// 2.推送数据
		pushService.pushRoom(uid, WSRespTypeEnum.USER_STATE_CHANGE.getType(), new UserStateVo(uid, userState == null? 0L: userState.getId()));
		return true;
	}
}
