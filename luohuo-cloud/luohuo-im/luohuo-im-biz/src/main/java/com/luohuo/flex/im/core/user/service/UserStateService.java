package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.entity.UserState;
import java.util.List;

/**
 * <p>
 * 用户在线状态表 服务类
 * </p>
 *
 * @author 乾乾
 */
public interface UserStateService {

	List<UserState> list();

	/**
	 * 用户状态改变 通知在线的所有人
	 * @param uid 登录人
	 * @param userStateId 改变的状态
	 */
	Boolean changeState(Long uid, Long userStateId);
}
