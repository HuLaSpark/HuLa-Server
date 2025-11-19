package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.entity.FeedComment;
import com.luohuo.flex.im.domain.entity.FeedLike;

/**
 * 朋友圈通知服务
 */
public interface FeedNotifyService {

    /**
     * 发送点赞通知
     * 通知逻辑：
     * 1. 获取朋友圈发布人的所有好友
     * 2. 过滤出与该朋友圈有互动关系的好友（已点赞或已评论）
     * 3. 发送 WebSocket 通知给这些好友
     *
     * @param feedLike 点赞记录
     */
    void notifyFeedLike(FeedLike feedLike);

    /**
     * 发送评论通知
     * 通知逻辑：
     * 1. 获取朋友圈发布人的所有好友
     * 2. 过滤出与该朋友圈有互动关系的好友（已点赞或已评论）
     * 3. 发送 WebSocket 通知给这些好友
     *
     * @param feedComment 评论记录
     */
    void notifyFeedComment(FeedComment feedComment);

    /**
     * 取消点赞通知
     *
     * @param feedId 朋友圈ID
     * @param uid    点赞人UID
     */
    void notifyFeedUnlike(Long feedId, Long uid);
}
