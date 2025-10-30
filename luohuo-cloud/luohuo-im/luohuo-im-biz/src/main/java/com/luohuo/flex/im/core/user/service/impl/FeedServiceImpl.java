package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.flex.common.cache.common.FeedMediaRelCacheKeyBuilder;
import com.luohuo.flex.common.cache.common.FeedTargetRelCacheKeyBuilder;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.core.chat.service.adapter.MemberAdapter;
import com.luohuo.flex.im.core.user.dao.FeedDao;
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
import com.luohuo.flex.im.core.user.service.FeedService;
import com.luohuo.flex.im.core.user.service.UserTargetRelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	/**
	 * @param feedList 朋友圈基础内容
	 * @return
	 */
	private List<FeedVo> buildFeedResp(List<Feed> feedList) {
		List<FeedVo> feedVos = new ArrayList<>();
		for (Feed feed : feedList) {
			FeedVo feedVo = new FeedVo();
			BeanUtil.copyProperties(feed, feedVo);

			if(!feed.getMediaType().equals(FeedEnum.WORD.getType())){
				CacheHashKey hashKey = FeedMediaRelCacheKeyBuilder.build(feed.getId());
				CacheResult<List<FeedMedia>> result = cachePlusOps.get(hashKey, t -> feedMediaDao.getMediaByFeedId(feed.getId()));
				List<FeedMedia> mediaList = result.getValue();
				if(CollUtil.isNotEmpty(mediaList)){
					feedVo.setUrls(mediaList.stream().sorted(Comparator.comparingInt(FeedMedia::getSort)).map(FeedMedia::getUrl).collect(Collectors.toList()));
				}
			}
			feedVos.add(feedVo);
		}
		return feedVos;
	}

	/**
	 * 游标刷新朋友圈列表
	 * @param request
	 * @param uid
	 */
	@Override
	public CursorPageBaseResp<FeedVo> getFeedPage(CursorPageBaseReq request, Long uid) {
		// 1. 查询朋友圈列表
		CursorPageBaseResp<Feed> page = feedDao.getFeedPage(uid, request);

		// 2. 合并朋友圈内容
		List<FeedVo> result = buildFeedResp(page.getList());
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

		// 3. 缓存权限+素材 告知 pushList 我发布了朋友圈
//		cachePlusOps.hDel(FeedMediaRelCacheKeyBuilder.build(feed.getId()));
//		cachePlusOps.hDel(FeedTargetRelCacheKeyBuilder.build(feed.getId()));
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
		// 1. 首先将朋友圈素材、权限删除
		feedMediaDao.delMediaByFeedId(feedId);
		feedTargetDao.delByFeedId(feedId);
		feedDao.removeById(feedId);

		// 2. 清空缓存
		cachePlusOps.hDel(FeedTargetRelCacheKeyBuilder.build(feedId));
		cachePlusOps.hDel(FeedMediaRelCacheKeyBuilder.build(feedId));
		return true;
	}

	public FeedVo getDetail(Long id) {
		return feedDao.getDetail(id);
	}

	/**
	 * 查看朋友圈
	 * @param feedId
	 * @return
	 */
	public FeedVo feedDetail(Long feedId) {
		FeedVo feed = getDetail(feedId);

		if(feed.getMediaType().equals(FeedEnum.WORD.getType())){
			CacheResult<Object> cacheResult = cachePlusOps.hGet(FeedMediaRelCacheKeyBuilder.build(feedId));
			List<FeedMedia> feedMediaList = cacheResult.asList();
			if(CollUtil.isEmpty(feedMediaList)){
				feedMediaList = feedMediaDao.getMediaByFeedId(feedId);
			}
			if (CollUtil.isNotEmpty(feedMediaList)){
				feed.setUrls(feedMediaList.stream().sorted(Comparator.comparingInt(FeedMedia::getSort)).map(FeedMedia::getUrl).collect(Collectors.toList()));
			}
		}

		return feed;
	}

	/**
	 * 获取朋友圈的可见权限
	 * @param feedId
	 * @return
	 */
	public FeedPermission getFeedPermission(Long uid, Long feedId) {
		FeedVo feedVo = feedDetail(feedId);
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

		// 3. 更新朋友圈的权限+素材
		saveFeed(param, uid, feed);
		return true;
	}
}
