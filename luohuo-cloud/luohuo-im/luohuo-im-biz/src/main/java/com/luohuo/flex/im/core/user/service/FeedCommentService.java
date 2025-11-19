package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.req.feed.FeedCommentReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedCommentVo;

import java.util.List;

/**
 * 朋友圈评论服务
 */
public interface FeedCommentService {

    /**
     * 发表评论
     * @param uid 操作人
     * @param req 评论请求
     * @return 是否成功
     */
    Boolean addComment(Long uid, FeedCommentReq req);

    /**
     * 删除评论
     * @param uid 操作人
     * @param commentId 评论ID
     * @return 是否成功
     */
    Boolean delComment(Long uid, Long commentId);

    /**
     * 分页查询朋友圈评论
     * @param feedId 朋友圈ID
     * @param request 分页请求
     * @return 评论分页列表
     */
    CursorPageBaseResp<FeedCommentVo> getCommentPage(Long feedId, CursorPageBaseReq request);

    /**
     * 获取朋友圈的评论列表（不分页，用于展示前几条）
     * @param feedId 朋友圈ID
     * @param limit 限制数量
     * @return 评论列表
     */
    List<FeedCommentVo> getCommentList(Long feedId, Integer limit);

    /**
     * 获取朋友圈的评论数量
     * @param feedId 朋友圈ID
     * @return 评论数量
     */
    Integer getCommentCount(Long feedId);
}
