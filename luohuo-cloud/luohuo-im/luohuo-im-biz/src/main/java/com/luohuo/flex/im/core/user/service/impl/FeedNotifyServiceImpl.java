package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.luohuo.flex.im.core.user.dao.FeedCommentDao;
import com.luohuo.flex.im.core.user.dao.FeedDao;
import com.luohuo.flex.im.core.user.dao.FeedLikeDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.core.user.service.FeedNotifyService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.entity.FeedComment;
import com.luohuo.flex.im.domain.entity.FeedLike;
import com.luohuo.flex.model.entity.WSRespTypeEnum;
import com.luohuo.flex.model.entity.WsBaseResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 朋友圈通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedNotifyServiceImpl implements FeedNotifyService {

	private final FeedLikeDao feedLikeDao;
	private final FeedCommentDao feedCommentDao;
	private final FeedDao feedDao;
	private final UserFriendDao userFriendDao;
	private final PushService pushService;
	private final UserSummaryCache userSummaryCache;

	@Override
	public void notifyFeedLike(FeedLike feedLike) {
		try {
			// 1. 获取朋友圈信息
			Feed feed = feedDao.getById(feedLike.getFeedId());
			if (feed == null) return;

			// 2. 获取跟朋友圈有关系的好友
			Set<Long> interactiveUidSet = getInteractiveUid(feedLike.getFeedId(), feedLike.getUid());
			if (interactiveUidSet == null) return;

			// 3. 获取操作人信息
			SummeryInfoDTO userInfo = userSummaryCache.get(feedLike.getUid());

			// 4. 构建通知消息 - 点赞：uid（操作人）、feedId、feedOwnerUid（朋友圈发布人）、operatorName、operatorAvatar（无 comment 字段）
			Map<String, Object> data = new HashMap<>();
			data.put("uid", feedLike.getUid());  // 操作人UID（点赞的人）
			data.put("feedId", feedLike.getFeedId());
			data.put("feedOwnerUid", feed.getUid());  // 朋友圈发布人UID（被点赞的人）
			if (userInfo != null) {
				data.put("operatorName", userInfo.getName());
				data.put("operatorAvatar", userInfo.getAvatar());
			}

			sendFeedNotify(data, interactiveUidSet, feedLike.getUid(), "点赞");
		} catch (Exception e) {
			log.error("❌ 发送点赞通知失败", e);
		}
	}

	@Nullable
	private Set<Long> getInteractiveUid(Long feedId, Long operatorUid) {
		// 1. 获取朋友圈信息
		Feed feed = feedDao.getById(feedId);
		if (feed == null) {
			return null;
		}

		// 2. 获取朋友圈发布人的所有好友
		List<Long> friendUidList = userFriendDao.getAllFriendIdsByUid(feed.getUid());
		if (CollUtil.isEmpty(friendUidList)) {
			return null;
		}

		// 3. 获取与该朋友圈有互动关系的好友（已点赞或已评论）
		Set<Long> interactiveUidSet = getInteractiveUidSet(feedId, friendUidList);

		// 4. 添加发朋友圈的人
		interactiveUidSet.add(feed.getUid());

		// 5. 添加操作人自己（这样操作人可以在其他设备上实时看到自己的操作）
		interactiveUidSet.add(operatorUid);

		if (CollUtil.isEmpty(interactiveUidSet)) {
			return null;
		}
		return interactiveUidSet;
	}

	@Override
	public void notifyFeedComment(FeedComment feedComment) {
		try {
			// 1. 获取朋友圈信息
			Feed feed = feedDao.getById(feedComment.getFeedId());
			if (feed == null) return;

			// 2. 获取跟朋友圈有关系的好友
			Set<Long> interactiveUidSet = getInteractiveUid(feedComment.getFeedId(), feedComment.getUid());
			if (interactiveUidSet == null) return;

			// 3. 获取操作人信息
			SummeryInfoDTO userInfo = userSummaryCache.get(feedComment.getUid());

			// 4. 构建通知消息 - 评论：uid（操作人）、feedId、feedOwnerUid（朋友圈发布人）、comment、replyCommentId、operatorName、operatorAvatar
			Map<String, Object> data = new HashMap<>();
			data.put("replyCommentId", feedComment.getReplyCommentId());
			data.put("uid", feedComment.getUid());  // 操作人UID（评论的人）
			data.put("feedId", feedComment.getFeedId());
			data.put("feedOwnerUid", feed.getUid());  // 朋友圈发布人UID（被评论的人）
			data.put("comment", feedComment.getContent());
			if (userInfo != null) {
				data.put("operatorName", userInfo.getName());
				data.put("operatorAvatar", userInfo.getAvatar());
			}

			sendFeedNotify(data, interactiveUidSet, feedComment.getUid(), "评论");
		} catch (Exception e) {
			log.error("❌ 发送评论通知失败", e);
		}
	}

	/**
	 * 发送朋友圈通知（统一处理点赞和评论）
	 * 前端通过判断 comment 字段是否存在来区分点赞还是评论
	 */
	private void sendFeedNotify(Map<String, Object> data, Set<Long> interactiveUidSet, Long operatorUid, String type) {
		WsBaseResp<Map<String, Object>> resp = new WsBaseResp<>();
		resp.setType(WSRespTypeEnum.FEED_NOTIFY.getType());
		resp.setData(data);

		pushService.sendPushMsg(resp, new ArrayList<>(interactiveUidSet), operatorUid);

		Long feedId = (Long) data.get("feedId");
		log.info("✅ {}通知已发送，朋友圈ID: {}，操作人: {}，通知人数: {}", type, feedId, operatorUid, interactiveUidSet.size());
	}

	@Override
	public void notifyFeedUnlike(Long feedId, Long uid) {
		try {
			// 1. 获取朋友圈信息
			Feed feed = feedDao.getById(feedId);
			if (feed == null) return;

			// 2. 获取跟朋友圈有关系的好友
			Set<Long> interactiveUidSet = getInteractiveUid(feedId, uid);
			if (interactiveUidSet == null) return;

			// 3. 获取操作人信息
			SummeryInfoDTO userInfo = userSummaryCache.get(uid);

			// 4. 构建通知消息 - 取消点赞：uid（操作人）、feedId、feedOwnerUid（朋友圈发布人）、isUnlike=true、operatorName、operatorAvatar
			Map<String, Object> data = new HashMap<>();
			data.put("uid", uid);  // 操作人UID（取消点赞的人）
			data.put("feedId", feedId);
			data.put("feedOwnerUid", feed.getUid());  // 朋友圈发布人UID（被取消点赞的人）
			data.put("isUnlike", true);
			if (userInfo != null) {
				data.put("operatorName", userInfo.getName());
				data.put("operatorAvatar", userInfo.getAvatar());
			}

			sendFeedNotify(data, interactiveUidSet, uid, "取消点赞");
		} catch (Exception e) {
			log.error("❌ 发送取消点赞通知失败", e);
		}
	}

	/**
	 * 获取与该朋友圈有互动关系的好友集合
	 * 互动关系包括：已点赞或已评论
	 */
	private Set<Long> getInteractiveUidSet(Long feedId, List<Long> friendUidList) {
		Set<Long> interactiveUidSet = new HashSet<>();

		// 1. 获取所有点赞人
		List<Long> likeUidList = feedLikeDao.getLikeUidList(feedId);
		interactiveUidSet.addAll(likeUidList);

		// 2. 获取所有评论人
		List<FeedComment> commentList = feedCommentDao.getCommentListByFeedId(feedId);
		Set<Long> commentUidSet = commentList.stream()
				.map(FeedComment::getUid)
				.collect(Collectors.toSet());
		interactiveUidSet.addAll(commentUidSet);

		// 3. 过滤出是好友的用户
		interactiveUidSet.retainAll(friendUidList);

		return interactiveUidSet;
	}
}
