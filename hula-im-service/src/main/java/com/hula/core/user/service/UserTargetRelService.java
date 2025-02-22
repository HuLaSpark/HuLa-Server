package com.hula.core.user.service;

import com.hula.core.chat.domain.vo.request.room.TargetVo;
import com.hula.core.chat.domain.vo.request.room.UserTargetRelParam;
import java.util.List;

public interface UserTargetRelService {

	/**
	 * 修改人员的标签
	 * @param param
	 * @return
	 */
	Boolean editTarget(Long uid, UserTargetRelParam param);

	/**
	 * 获取自己与好友的标签
	 * @param uid 自己的id
	 * @param friendId 好友的id
	 */
	List<Long> getRelTarget(Long uid, Long friendId);

	List<TargetVo> detail(Long uid, Long friendId);
}
