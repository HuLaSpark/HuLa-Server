package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.flex.common.cache.common.FeedLikeCacheKeyBuilder;
import com.luohuo.flex.im.core.user.dao.FeedDao;
import com.luohuo.flex.im.core.user.dao.FeedLikeDao;
import com.luohuo.flex.im.core.user.service.FeedLikeService;
import com.luohuo.flex.im.core.user.service.FeedNotifyService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.entity.FeedLike;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedLikeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 朋友圈点赞服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedLikeServiceImpl implements FeedLikeService {

	private final FeedLikeDao feedLikeDao;
	private final FeedDao feedDao;
	private final CachePlusOps cachePlusOps;
	private final UserSummaryCache userSummaryCache;
	private final FeedNotifyService feedNotifyService;

	@Override
	@Transactional
	public Boolean setLike(Long uid, Long feedId, Integer actType) {
		// 1. 校验朋友圈是否存在
		Feed feed = feedDao.getById(feedId);
		if (Objects.isNull(feed)) {
			throw new BizException("朋友圈不存在");
		}

		// 2. 查询是否已经点赞过
		FeedLike oldLike = feedLikeDao.get(uid, feedId);

		// 3. 根据操作类型处理
		if (actType == 1) {
			// 点赞
			if (Objects.isNull(oldLike)) {
				// 新增点赞记录
				FeedLike feedLike = FeedLike.builder()
						.feedId(feedId)
						.uid(uid)
						.build();
				feedLikeDao.save(feedLike);

				// 发送点赞通知
				feedNotifyService.notifyFeedLike(feedLike);
			}
			// 如果已经点赞过，则不做任何操作（幂等性）
		} else if (actType == 2) {
			// 取消点赞
			if (Objects.isNull(oldLike)) {
				// 没有点赞记录，直接返回
				return true;
			}
			feedLikeDao.removeById(oldLike.getId());

			// 发送取消点赞通知
			feedNotifyService.notifyFeedUnlike(feedId, uid);
		} else {
			throw new BizException("操作类型错误");
		}

		// 4. 清除缓存（在事务提交后执行，确保数据库已更新）
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				cachePlusOps.del(FeedLikeCacheKeyBuilder.build(feedId));
				log.info("✅ 点赞缓存已清除，朋友圈ID: {}", feedId);
			}
		});

		return true;
	}

	@Override
	public List<FeedLikeVo> getLikeList(Long feedId) {
		// 1. 从缓存获取点赞用户ID列表
		CacheHashKey hashKey = FeedLikeCacheKeyBuilder.build(feedId);
		CacheResult<List<Long>> result = cachePlusOps.get(hashKey, t -> feedLikeDao.getLikeUidList(feedId));
		List<Long> likeUidList = result.getValue();

		if (CollUtil.isEmpty(likeUidList)) {
			return new ArrayList<>();
		}

		// 2. 批量获取用户信息
		Map<Long, SummeryInfoDTO> userInfoMap = userSummaryCache.getBatch(likeUidList);

		// 3. 组装返回结果
		return likeUidList.stream()
				.map(uid -> {
					SummeryInfoDTO userInfo = userInfoMap.get(uid);
					if (Objects.isNull(userInfo)) {
						return null;
					}
					return FeedLikeVo.builder()
							.uid(uid)
							.userName(userInfo.getName())
							.userAvatar(userInfo.getAvatar())
							.build();
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public Integer getLikeCount(Long feedId) {
		return feedLikeDao.getLikeCount(feedId);
	}

	@Override
	public Boolean hasLiked(Long uid, Long feedId) {
		FeedLike feedLike = feedLikeDao.get(uid, feedId);
		return Objects.nonNull(feedLike);
	}
}
