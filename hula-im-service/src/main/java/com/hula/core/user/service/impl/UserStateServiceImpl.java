package com.hula.core.user.service.impl;

import com.hula.core.user.dao.UserDao;
import com.hula.core.user.dao.UserStateDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserState;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.user.UserStateVo;
import com.hula.core.user.service.UserStateService;
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
	private UserDao userDao;
	private PushService pushService;

	@Override
	public List<UserState> list() {
		return userStateDao.list();
	}

	@Override
	public Boolean changeState(Long uid, Long userStateId) {
		User user = userDao.getById(uid);
		// 如果检测到状态没有改变，那么则不动
		if (user.getUserStateId().equals(userStateId)){
			return true;
		}

		UserState userState = userStateDao.getById(userStateId);
		Boolean changeUserState = userDao.changeUserState(uid, userStateId);

		if (changeUserState){
			WsBaseResp<UserStateVo> wsBaseResp = new WsBaseResp();
			wsBaseResp.setType(WSRespTypeEnum.USER_STATE_CHANGE.getType());
			wsBaseResp.setData(new UserStateVo(uid, userState.getTitle(), userState.getUrl()));
			pushService.sendPushMsg(wsBaseResp, uid);
			return true;
		}else {
			throw new RuntimeException("用户状态更新失败");
		}
	}
}
