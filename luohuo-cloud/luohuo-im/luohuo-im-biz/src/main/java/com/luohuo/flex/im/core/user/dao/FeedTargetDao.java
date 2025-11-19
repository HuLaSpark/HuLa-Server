package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.FeedTarget;
import com.luohuo.flex.im.core.user.mapper.FeedTargetMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedTargetDao extends ServiceImpl<FeedTargetMapper, FeedTarget> {

	/**
	 * 删除朋友圈和标签的管理
	 *
	 * @param feedId
	 * @return
	 */
	public boolean delByFeedId(Long feedId) {
		return remove(new LambdaQueryWrapper<FeedTarget>()
				.eq(FeedTarget::getFeedId, feedId));
	}

	public List<FeedTarget> selectFeedTargets(Long feedId) {
		LambdaQueryWrapper<FeedTarget> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.eq(FeedTarget::getFeedId, feedId).select(FeedTarget::getTargetId, FeedTarget::getType);
		return baseMapper.selectList(queryWrapper);
	}
}
