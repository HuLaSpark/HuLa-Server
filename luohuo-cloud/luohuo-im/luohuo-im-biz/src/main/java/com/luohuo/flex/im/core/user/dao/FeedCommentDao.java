package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.core.user.mapper.FeedCommentMapper;
import com.luohuo.flex.im.domain.entity.FeedComment;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 朋友圈评论 Dao
 */
@Service
public class FeedCommentDao extends ServiceImpl<FeedCommentMapper, FeedComment> {

	/**
	 * 获取朋友圈的评论数量
	 */
	public Integer getCommentCount(Long feedId) {
		return Math.toIntExact(lambdaQuery()
				.eq(FeedComment::getFeedId, feedId)
				.count());
	}

	/**
	 * 游标分页查询朋友圈评论（升序排列，最新评论在最后）
	 */
	public CursorPageBaseResp<FeedComment> getCommentPage(Long feedId, CursorPageBaseReq request) {
		// 使用 CursorUtils 进行游标分页（默认降序）
		CursorPageBaseResp<FeedComment> result = CursorUtils.getCursorPageByMysql(
				this,
				request,
				wrapper -> wrapper.eq(FeedComment::getFeedId, feedId),
				FeedComment::getCreateTime
		);

		// 反向排序列表，使最新评论在最后
		Collections.reverse(result.getList());

		return result;
	}

	/**
	 * 获取某条朋友圈的所有评论（不分页，用于缓存）
	 * 注意：MyBatis Plus 会自动过滤已删除的记录（通过 @TableLogic 注解）
	 */
	public List<FeedComment> getCommentListByFeedId(Long feedId) {
		return lambdaQuery()
				.eq(FeedComment::getFeedId, feedId)
				.orderByAsc(FeedComment::getCreateTime)
				.list();
	}

	/**
	 * 删除朋友圈的所有评论
	 */
	public boolean delByFeedId(Long feedId) {
		return remove(new LambdaQueryWrapper<FeedComment>()
				.eq(FeedComment::getFeedId, feedId));
	}
}
