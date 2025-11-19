package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.core.user.mapper.FeedLikeMapper;
import com.luohuo.flex.im.domain.entity.FeedLike;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 朋友圈点赞 Dao
 */
@Service
public class FeedLikeDao extends ServiceImpl<FeedLikeMapper, FeedLike> {

	/**
	 * 获取用户对某条朋友圈的点赞记录
	 */
	public FeedLike get(Long uid, Long feedId) {
		return lambdaQuery()
				.eq(FeedLike::getUid, uid)
				.eq(FeedLike::getFeedId, feedId)
				.one();
	}

	/**
	 * 获取朋友圈的点赞数量
	 */
	public Integer getLikeCount(Long feedId) {
		return Math.toIntExact(lambdaQuery()
				.eq(FeedLike::getFeedId, feedId)
				.count());
	}

	/**
	 * 获取某条朋友圈的所有点赞用户ID列表
	 */
	public List<Long> getLikeUidList(Long feedId) {
		return lambdaQuery()
				.eq(FeedLike::getFeedId, feedId)
				.select(FeedLike::getUid)
				.list()
				.stream()
				.map(FeedLike::getUid)
				.toList();
	}

	/**
	 * 删除朋友圈的所有点赞
	 */
	public boolean delByFeedId(Long feedId) {
		return remove(new LambdaQueryWrapper<FeedLike>()
				.eq(FeedLike::getFeedId, feedId));
	}
}
