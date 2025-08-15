package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.vo.request.room.TargetVo;
import com.luohuo.flex.im.domain.vo.request.room.UserTargetRelParam;
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

	/**
	 * 查询标签详情
	 * @return
	 */
	List<TargetVo> detail(Long uid, Long friendId);

	/**
	 * 获取到朋友圈可见的人员
	 * @param targetIds 标签集合
	 */
	List<Long> getFeedUidList(List<Long> targetIds, Long uid);
}
