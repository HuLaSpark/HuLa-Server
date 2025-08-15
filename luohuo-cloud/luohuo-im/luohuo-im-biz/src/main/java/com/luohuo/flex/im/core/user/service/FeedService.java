package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.req.feed.FeedParam;
import com.luohuo.flex.im.domain.vo.req.feed.FeedPermission;
import com.luohuo.flex.im.domain.vo.req.feed.FeedVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * 朋友圈核心服务
 */
public interface FeedService {

	/**
	 * 游标刷新朋友圈列表
	 * @param request
	 * @param uid
	 */
	CursorPageBaseResp<FeedVo> getFeedPage(CursorPageBaseReq request, Long uid);

	/**
	 * 发布朋友圈
	 * @param uid 操作人
	 * @param param
	 * @return
	 */
	Boolean pushFeed(Long uid, FeedParam param);

	/**
	 * 删除朋友圈
	 * @param feedId
	 * @return
	 */
	@Transactional
	Boolean delFeed(Long feedId);

	FeedVo getDetail(Long id);

	/**
	 * 查看朋友圈
	 * @param feedId
	 * @return
	 */
	FeedVo feedDetail(Long feedId);

	/**
	 * 获取朋友圈的可见权限
	 * @param uid 操作人
	 * @param feedId
	 * @return
	 */
	FeedPermission getFeedPermission(Long uid, Long feedId);

	/**
	 * 修改朋友圈的信息
	 * @param param
	 * @return
	 */
	Boolean editFeed(Long uid, FeedParam param);
}
