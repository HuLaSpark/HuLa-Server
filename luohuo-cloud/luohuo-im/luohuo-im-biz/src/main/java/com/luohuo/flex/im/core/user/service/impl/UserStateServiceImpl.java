package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.luohuo.flex.im.core.user.dao.UserStateDao;
import com.luohuo.flex.im.domain.entity.UserState;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserStateVo;
import com.luohuo.flex.im.core.user.service.UserService;
import com.luohuo.flex.im.core.user.service.UserStateService;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
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
	private UserSummaryCache userSummaryCache;
	private UserCache userCache;
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
		Boolean changeUserState = userService.changeUserState(uid, userStateId);

		if (ObjectUtil.isNotNull(userState) && changeUserState){
			// 1.清除缓存
			userCache.userInfoChange(uid);
			userSummaryCache.delete(uid);

			// 2.推送数据
			pushService.pushFriends(uid, WSRespTypeEnum.USER_STATE_CHANGE.getType(), new UserStateVo(uid, userState.getId()));
			return true;
		}else {
			throw new RuntimeException("用户状态更新失败");
		}
	}
}
