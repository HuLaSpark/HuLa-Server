package com.hula.core.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.hula.core.user.dao.UserStateDao;
import com.hula.core.user.domain.entity.UserState;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.user.UserInfoResp;
import com.hula.core.user.domain.vo.resp.user.UserStateVo;
import com.hula.core.user.service.UserService;
import com.hula.core.user.service.UserStateService;
import com.hula.core.user.service.cache.UserSummaryCache;
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
			userSummaryCache.delete(uid);

			// 2.推送数据
			WsBaseResp<UserStateVo> wsBaseResp = new WsBaseResp();
			wsBaseResp.setType(WSRespTypeEnum.USER_STATE_CHANGE.getType());
			wsBaseResp.setData(new UserStateVo(uid, userState.getId()));
			pushService.sendPushMsg(wsBaseResp, uid);
			return true;
		}else {
			throw new RuntimeException("用户状态更新失败");
		}
	}
}
