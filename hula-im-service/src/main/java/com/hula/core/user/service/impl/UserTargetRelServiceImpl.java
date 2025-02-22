package com.hula.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.hula.common.annotation.RedissonLock;
import com.hula.core.user.domain.entity.UserTargetRel;
import com.hula.core.chat.domain.vo.request.room.TargetVo;
import com.hula.core.chat.domain.vo.request.room.UserTargetRelParam;
import com.hula.core.user.dao.UserTargetRelDao;
import com.hula.core.user.service.TargetService;
import com.hula.core.user.service.UserTargetRelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTargetRelServiceImpl implements UserTargetRelService {

	private final TargetService targetService;
	private final UserTargetRelDao userTargetRelDao;

	/**
	 * 修改人员的标签
	 * @param param
	 * @return
	 */
	@Transactional
	@RedissonLock(prefixKey = "target:", key = "#param.targetUId")
	public Boolean editTarget(Long uid, UserTargetRelParam param){
		List<UserTargetRel> userTargetRels = new ArrayList<>();
		Long friendId = param.getFriendId();
		List<Long> targetIds = param.getTargetIds();

		// 将之前关联的进行删除
		userTargetRelDao.removeByIds(getRelTarget(uid, friendId));

		if (CollUtil.isNotEmpty(targetIds)){
			targetIds.forEach(item -> {
				UserTargetRel userTargetRel = new UserTargetRel();
				userTargetRel.setUid(uid);
				userTargetRel.setTargetId(item);
				userTargetRel.setFriendId(friendId);
				userTargetRels.add(userTargetRel);
			});
			return userTargetRelDao.saveBatch(userTargetRels);
		}
		return true;
	}

	/**
	 * 获取自己与好友的标签
	 * @param uid 自己的id
	 * @param friendId 好友的id
	 */
	@Override
	public List<Long> getRelTarget(Long uid, Long friendId) {
		 return userTargetRelDao.getRelTarget(uid, friendId);
	}

	@Override
	public List<TargetVo> detail(Long uid, Long friendId) {
		List<Long> ids = getRelTarget(uid, friendId);

		if(CollUtil.isNotEmpty(ids)) {
			return targetService.getTargetList(ids);
		}
		return new ArrayList<>();
	}
}
