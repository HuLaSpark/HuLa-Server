package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.vo.resp.feed.FeedLikeVo;

import java.util.List;

/**
 * 朋友圈点赞服务
 */
public interface FeedLikeService {

    /**
     * 点赞或取消点赞
     * @param uid 操作人
     * @param feedId 朋友圈ID
     * @param actType 操作类型 1-点赞 2-取消点赞
     * @return 是否成功
     */
    Boolean setLike(Long uid, Long feedId, Integer actType);

    /**
     * 获取朋友圈的点赞列表
     * @param feedId 朋友圈ID
     * @return 点赞用户列表
     */
    List<FeedLikeVo> getLikeList(Long feedId);

    /**
     * 获取朋友圈的点赞数量
     * @param feedId 朋友圈ID
     * @return 点赞数量
     */
    Integer getLikeCount(Long feedId);

    /**
     * 判断用户是否已点赞
     * @param uid 用户ID
     * @param feedId 朋友圈ID
     * @return 是否已点赞
     */
    Boolean hasLiked(Long uid, Long feedId);
}
