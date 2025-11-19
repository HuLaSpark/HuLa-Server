package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.flex.common.cache.common.FeedCommentCacheKeyBuilder;
import com.luohuo.flex.common.cache.common.FeedLikeCacheKeyBuilder;
import com.luohuo.flex.common.cache.common.FeedMediaRelCacheKeyBuilder;
import com.luohuo.flex.common.cache.common.FeedTargetRelCacheKeyBuilder;
import com.luohuo.flex.im.domain.entity.FeedLike;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.im.domain.vo.req.feed.FeedPageReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.core.chat.service.adapter.MemberAdapter;
import com.luohuo.flex.im.core.user.dao.FeedCommentDao;
import com.luohuo.flex.im.core.user.dao.FeedDao;
import com.luohuo.flex.im.core.user.dao.FeedLikeDao;
import com.luohuo.flex.im.core.user.dao.FeedMediaDao;
import com.luohuo.flex.im.core.user.dao.FeedTargetDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.entity.FeedMedia;
import com.luohuo.flex.im.domain.entity.FeedTarget;
import com.luohuo.flex.im.domain.enums.FeedEnum;
import com.luohuo.flex.im.domain.enums.FeedPermissionEnum;
import com.luohuo.flex.im.domain.vo.req.feed.FeedParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedPermission;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedLikeVo;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedCommentVo;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.core.user.service.FeedService;
import com.luohuo.flex.im.core.user.service.FeedCommentService;
import com.luohuo.flex.im.core.user.service.UserTargetRelService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 朋友圈核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

	private final PushService pushService;
	private final FeedDao feedDao;
	private final FeedMediaDao feedMediaDao;
	private final UserTargetRelService userTargetRelService;
	private final UserFriendDao userFriendDao;
	private final CachePlusOps cachePlusOps;
	private final FeedTargetDao feedTargetDao;
	private final FeedCommentDao feedCommentDao;
	private final FeedCommentService feedCommentService;
	private final FeedLikeDao feedLikeDao;
	private final UserSummaryCache userSummaryCache;

	/**
	 * @param feedList 朋友圈基础内容
	 * @param currentUid 当前用户ID
	 * @return
	 */
	private List<FeedVo> buildFeedResp(List<Feed> feedList, Long currentUid) {
		return buildFeedResp(feedList, false, currentUid);
	}

	/**
	 * @param feedList 朋友圈基础内容
	 * @param currentUid 当前用户ID（用于判断是否已点赞）
	 * @return
	 */
	private List<FeedVo> buildFeedResp(List<Feed> feedList, boolean isDetail, Long currentUid) {
		List<FeedVo> feedVos = new ArrayList<>();

		// 批量获取所有发布者的用户信息
		Set<Long> userIds = feedList.stream().map(Feed::getUid).collect(Collectors.toSet());
		Map<Long, SummeryInfoDTO> userInfoMap = userSummaryCache.getBatch(new ArrayList<>(userIds));

		// 批量获取当前用户对所有朋友圈的点赞状态
		Map<Long, Boolean> likeStatusMap = new HashMap<>();
		if (CollUtil.isNotEmpty(feedList)) {
			List<Long> feedIds = feedList.stream().map(Feed::getId).collect(Collectors.toList());
			for (Long feedId : feedIds) {
				FeedLike like = feedLikeDao.get(currentUid, feedId);
				likeStatusMap.put(feedId, Objects.nonNull(like));
			}
		}

		for (Feed feed : feedList) {
			FeedVo feedVo = new FeedVo();
			BeanUtil.copyProperties(feed, feedVo);

			// 添加发布者信息
			SummeryInfoDTO userInfo = userInfoMap.get(feed.getUid());
			if (ObjectUtil.isNotNull(userInfo)) {
				feedVo.setUserName(userInfo.getName());
				feedVo.setUserAvatar(userInfo.getAvatar());
			}

			// 添加媒体信息
			if(!feed.getMediaType().equals(FeedEnum.WORD.getType())){
				CacheHashKey hashKey = FeedMediaRelCacheKeyBuilder.build(feed.getId());
				CacheResult<List<FeedMedia>> result = cachePlusOps.get(hashKey, t -> feedMediaDao.getMediaByFeedId(feed.getId()));
				List<FeedMedia> mediaList = result.getValue();
				if(CollUtil.isNotEmpty(mediaList)){
					feedVo.setUrls(mediaList.stream().sorted(Comparator.comparingInt(FeedMedia::getSort)).map(FeedMedia::getUrl).collect(Collectors.toList()));
				}
			}

			// 添加点赞信息
			Integer likeCount = feedLikeDao.getLikeCount(feed.getId());
			feedVo.setLikeCount(likeCount);

			// 设置当前用户是否已点赞
			feedVo.setHasLiked(likeStatusMap.getOrDefault(feed.getId(), false));

			// 获取点赞列表（返回全部，前端按高度显示）
			List<Long> likeUidList = feedLikeDao.getLikeUidList(feed.getId());

			if (CollUtil.isNotEmpty(likeUidList)) {
				Map<Long, SummeryInfoDTO> likeUserInfoMap = userSummaryCache.getBatch(likeUidList);
				List<FeedLikeVo> likeList = likeUidList.stream()
						.map(uid -> {
							SummeryInfoDTO likeUserInfo = likeUserInfoMap.get(uid);
							if (ObjectUtil.isNull(likeUserInfo)) {
								return null;
							}
							return FeedLikeVo.builder()
									.uid(uid)
									.userName(likeUserInfo.getName())
									.userAvatar(likeUserInfo.getAvatar())
									.build();
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
				feedVo.setLikeList(likeList);
			}

			// 添加评论数量
			Integer commentCount = feedCommentDao.getCommentCount(feed.getId());
			feedVo.setCommentCount(commentCount);

			// 获取评论列表（返回全部，前端按高度显示）
			List<FeedCommentVo> commentList = feedCommentService.getCommentList(feed.getId(), null);
			if (CollUtil.isNotEmpty(commentList)) {
				feedVo.setCommentList(commentList);
			}

			feedVos.add(feedVo);
		}
		return feedVos;
	}

	@Override
	public CursorPageBaseResp<FeedVo> getFeedPage(FeedPageReq request, Long uid) {
		List<Long> friendIds = userFriendDao.getAllFriendIdsByUid(uid);
		friendIds.add(uid);

		CursorPageBaseResp<Feed> page = feedDao.getFeedPage(friendIds, request);

		List<Feed> filteredFeeds = page.getList().stream().filter(feed -> {
			if (feed.getUid().equals(uid)) {
				return true;
			}

			FeedPermissionEnum permission = FeedPermissionEnum.get(feed.getPermission());
			switch (permission) {
				case privacy:
					return false;
				case open:
					return true;
				case partVisible:
					List<FeedTarget> targets = feedTargetDao.selectFeedTargets(feed.getId());
					return targets.stream().anyMatch(t -> t.getType() == 2 && t.getTargetId().equals(uid));
				case notAnyone:
					List<FeedTarget> excludes = feedTargetDao.selectFeedTargets(feed.getId());
					return excludes.stream().noneMatch(t -> t.getType() == 2 && t.getTargetId().equals(uid));
				default:
					return false;
			}
		}).collect(Collectors.toList());

		if (StrUtil.isNotBlank(request.getUserName())) {
			String keyword = request.getUserName().trim();
			Map<Long, SummeryInfoDTO> userInfoMap = userSummaryCache.getBatch(
				filteredFeeds.stream().map(Feed::getUid).distinct().collect(Collectors.toList())
			);
			filteredFeeds = filteredFeeds.stream()
				.filter(feed -> {
					SummeryInfoDTO userInfo = userInfoMap.get(feed.getUid());
					return userInfo != null && StrUtil.contains(userInfo.getName(), keyword);
				})
				.collect(Collectors.toList());
		}

		List<FeedVo> result = buildFeedResp(filteredFeeds, uid);
		return CursorPageBaseResp.init(page, result, page.getTotal());
	}

	/**
	 * 发布朋友圈
	 */
	@Transactional
	public Boolean pushFeed(Long uid, FeedParam param){
		// 1. 保存朋友圈
		Feed feed = new Feed();
		feed.setUid(uid);
		feed.setContent(param.getContent());
		feed.setMediaType(param.getMediaType());
		feed.setPermission(param.getPermission());
		feedDao.save(feed);

		// 2. 告知所有人我的朋友圈更新了，所有人朋友圈按钮出现红点 + 头像
		saveFeed(param, uid, feed);
		return true;
	}

	/**
	 * 保存朋友圈权限+素材
	 * @param param 参数
	 * @param uid 操作人
	 * @param feed 朋友圈
	 */
	public void saveFeed(FeedParam param, Long uid, Feed feed) {
		saveFeed(param, uid, feed, false);
	}

	/**
	 * 保存朋友圈权限+素材
	 * @param param 参数
	 * @param uid 操作人
	 * @param feed 朋友圈
	 * @param needClearCache 是否需要清除缓存（编辑时为true，新建时为false）
	 */
	private void saveFeed(FeedParam param, Long uid, Feed feed, boolean needClearCache) {
		List<Long> pushList = new ArrayList<>();
		List<FeedTarget> feedTargets = new ArrayList<>();
		switch (FeedPermissionEnum.get(param.getPermission())){
			case open -> {
				// 1. 查询所有好友，排除【不让他看我, 他不看我】的好友
				List<Long> uidList = userFriendDao.getAllFriendIdsByUid(uid);
				uidList.removeAll(userFriendDao.getHideMyPosts(uid));
				uidList.removeAll(userFriendDao.getHideLookMe(uid));
				pushList.addAll(uidList);
			}
			case privacy -> pushList.add(uid);
			case notAnyone -> {
				// 2.1 查询所有好友, 排除【选中的标签+选中的好友+不让他看我+他不看我】
				List<Long> uidList = userFriendDao.getAllFriendIdsByUid(uid);
				if(CollUtil.isNotEmpty(param.getTargetIds())){
					feedTargets.addAll(saveFeedTarget(param.getTargetIds(), feed.getId(), uid));
					uidList.removeAll(userTargetRelService.getFeedUidList(param.getTargetIds(), uid));
				}
				if(CollUtil.isNotEmpty(param.getUidList())){
					feedTargets.addAll(saveFeedUser(param.getUidList(), feed.getId(), uid));
					uidList.removeAll(param.getUidList());
				}
				uidList.removeAll(userFriendDao.getHideMyPosts(uid));
				uidList.removeAll(userFriendDao.getHideLookMe(uid));
				pushList.addAll(uidList);
			}
			case partVisible -> {
				// 2.2 解析标签映射的好友 + 选中的好友；同时排除【不让他看我, 他不看我】
				Set<Long> uidList = new HashSet<>();
				if(CollUtil.isNotEmpty(param.getTargetIds())){
					feedTargets.addAll(saveFeedTarget(param.getTargetIds(), feed.getId(), uid));
					uidList.addAll(userTargetRelService.getFeedUidList(param.getTargetIds(), uid));
				}

				if(CollUtil.isNotEmpty(param.getUidList())){
					feedTargets.addAll(saveFeedUser(param.getUidList(), feed.getId(), uid));
					uidList.addAll(param.getUidList());
				}
				uidList.removeAll(userFriendDao.getHideMyPosts(uid));
				uidList.removeAll(userFriendDao.getHideLookMe(uid));
				pushList.addAll(uidList);
			}
		}

		// 2. 处理不同类型的朋友圈内容
		switch (FeedEnum.get(param.getMediaType())){
			case WORD -> log.info("发布了一条纯文字朋友圈~~");
			case IMAGE, VIDEO -> {
				List<String> images = param.getImages();
				if (CollUtil.isEmpty(images)){
					throw new RuntimeException("请至少上传一条素材!");
				}
				feedMediaDao.batchSaveMedia(feed.getId(), images, param.getMediaType());
			}
		}

		// 3. 清除缓存
		if (needClearCache) {
			cachePlusOps.del(FeedMediaRelCacheKeyBuilder.build(feed.getId()));
			cachePlusOps.del(FeedTargetRelCacheKeyBuilder.build(feed.getId()));
		}

		// 4. 告知 pushList 我发布了朋友圈
		pushService.sendPushMsg(MemberAdapter.buildFeedPushWS(uid), pushList, uid);
	}

	/**
	 * 保存朋友圈的标签
	 * @param targetIds 标签id
	 * @param feedId 朋友圈ID
	 * @param uid 用户id
	 */
	private List<FeedTarget> saveFeedTarget(List<Long> targetIds, Long feedId, Long uid) {
		List<FeedTarget> feedTargetList = new ArrayList<>();
		targetIds.forEach(item -> {
			FeedTarget feedTarget = new FeedTarget();
			feedTarget.setFeedId(feedId);
			feedTarget.setTargetId(item);
			feedTarget.setType(1);
			feedTargetList.add(feedTarget);
		});
		feedTargetDao.saveBatch(feedTargetList);
		return feedTargetList;
	}

	/**
	 * 保存朋友圈关联的用户id
	 * @param uidList 用户id集合
	 * @param feedId 朋友圈id
	 * @param uid 操作人id
	 */
	private List<FeedTarget> saveFeedUser(List<Long> uidList, Long feedId, Long uid) {
		List<FeedTarget> feedTargetList = new ArrayList<>();
		uidList.forEach(item -> {
			FeedTarget feedTarget = new FeedTarget();
			feedTarget.setFeedId(feedId);
			feedTarget.setTargetId(item);
			feedTarget.setType(2);
			feedTargetList.add(feedTarget);
		});
		feedTargetDao.saveBatch(feedTargetList);
		return feedTargetList;
	}

	/**
	 * 删除朋友圈
	 * @param feedId
	 * @return
	 */
	@Transactional
	public Boolean delFeed(Long feedId){
		// 1. 首先将朋友圈素材、权限、评论、点赞删除
		feedMediaDao.delMediaByFeedId(feedId);
		feedTargetDao.delByFeedId(feedId);
		feedCommentDao.delByFeedId(feedId);
		feedLikeDao.delByFeedId(feedId);
		feedDao.removeById(feedId);

		// 2. 清空缓存
		cachePlusOps.del(FeedTargetRelCacheKeyBuilder.build(feedId));
		cachePlusOps.del(FeedMediaRelCacheKeyBuilder.build(feedId));
		cachePlusOps.del(FeedCommentCacheKeyBuilder.build(feedId));
		cachePlusOps.del(FeedLikeCacheKeyBuilder.build(feedId));
		return true;
	}

	public FeedVo getDetail(Long id) {
		return feedDao.getDetail(id);
	}

	/**
	 * 查看朋友圈
	 * @param feedId 朋友圈ID
	 * @param uid 当前用户ID
	 * @return
	 */
	public FeedVo feedDetail(Long feedId, Long uid) {
		Feed feed = feedDao.getById(feedId);
		if (ObjectUtil.isNull(feed)) {
			return null;
		}

		// 使用 buildFeedResp 方法构建详情页的响应（isDetail=true，显示全部点赞）
		List<FeedVo> feedVos = buildFeedResp(List.of(feed), true, uid);
		return feedVos.isEmpty() ? null : feedVos.get(0);
	}

	/**
	 * 获取朋友圈的可见权限
	 * @param feedId
	 * @return
	 */
	public FeedPermission getFeedPermission(Long uid, Long feedId) {
		FeedVo feedVo = feedDetail(feedId, uid);
		if(ObjectUtil.isNull(feedVo)){
			throw new RuntimeException("请选择朋友圈!");
		}

		if(!feedVo.getUid().equals(uid)){
			throw new RuntimeException("只能查看自己的朋友圈!");
		}

		// 处理朋友圈权限
		if(feedVo.getPermission().equals(FeedPermissionEnum.partVisible.getType()) || feedVo.getPermission().equals(FeedPermissionEnum.notAnyone.getType())){
			CacheResult<Object> cacheResult = cachePlusOps.hGet(FeedTargetRelCacheKeyBuilder.build(feedId), t -> feedTargetDao.selectFeedTargets(feedId));
			List<FeedTarget> feedTargets = cacheResult.asList();

			List<Long> taggetList = feedTargets.stream().filter(item -> item.getType().equals(1)).map(FeedTarget::getTargetId).collect(Collectors.toUnmodifiableList());
			List<Long> userList = feedTargets.stream().filter(item -> item.getType().equals(2)).map(FeedTarget::getTargetId).collect(Collectors.toUnmodifiableList());
			return new FeedPermission(taggetList, userList);
		}
		return new FeedPermission();
	}

	/**
	 * 修改朋友圈的信息
	 * @param param
	 * @return
	 */
	public Boolean editFeed(Long uid, FeedParam param){
		Feed feed = feedDao.getById(param.getId());
		if(ObjectUtil.isNull(feed)){
			throw new BizException("请选择一条朋友圈!");
		}

		if(!feed.getUid().equals(uid)){
			throw new BizException("只能编辑自己的朋友圈!");
		}

		// 1. 更新朋友圈基础信息
		feed.setMediaType(param.getMediaType());
		feed.setPermission(param.getPermission());
		feed.setContent(param.getContent());
		feedDao.updateById(feed);

		// 2. 删除之前的权限+素材
		feedTargetDao.delByFeedId(feed.getId());
		feedMediaDao.delMediaByFeedId(feed.getId());

		// 3. 更新朋友圈的权限+素材（编辑场景，需要清除缓存）
		saveFeed(param, uid, feed, true);
		return true;
	}
}
