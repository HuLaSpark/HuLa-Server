package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.vo.req.feed.FeedPageReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.req.feed.FeedParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedPermission;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * 朋友圈核心服务
 */
public interface FeedService {

	CursorPageBaseResp<FeedVo> getFeedPage(FeedPageReq request, Long uid);

	Boolean pushFeed(Long uid, FeedParam param);

	@Transactional
	Boolean delFeed(Long feedId);

	FeedVo getDetail(Long id);

	FeedVo feedDetail(Long feedId, Long uid);

	FeedPermission getFeedPermission(Long uid, Long feedId);

	Boolean editFeed(Long uid, FeedParam param);
}
