package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.im.domain.entity.UserTargetRel;
import com.luohuo.flex.im.domain.vo.request.room.TargetVo;
import com.luohuo.flex.im.domain.vo.request.room.UserTargetRelParam;
import com.luohuo.flex.im.core.user.dao.UserTargetRelDao;
import com.luohuo.flex.im.core.user.service.TargetService;
import com.luohuo.flex.im.core.user.service.UserTargetRelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	/**
	 * 获取到朋友圈可见的人员
	 * @param targetIds 标签集合
	 * @return
	 */
	public List<Long> getFeedUidList(List<Long> targetIds, Long uid) {
		return userTargetRelDao.getBaseMapper().selectObjs(new LambdaQueryWrapper<UserTargetRel>()
						.select(UserTargetRel::getFriendId)
						.in(UserTargetRel::getTargetId, targetIds)
						.eq(UserTargetRel::getUid, uid))
				.stream()
				.map(o -> (Long) o)
				.collect(Collectors.toList());
	}
}
