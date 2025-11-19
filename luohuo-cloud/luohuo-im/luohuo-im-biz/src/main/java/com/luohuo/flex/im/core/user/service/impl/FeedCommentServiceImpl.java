package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.flex.common.cache.common.FeedCommentCacheKeyBuilder;
import com.luohuo.flex.im.core.user.dao.FeedCommentDao;
import com.luohuo.flex.im.core.user.dao.FeedDao;
import com.luohuo.flex.im.core.user.service.FeedCommentService;
import com.luohuo.flex.im.core.user.service.FeedNotifyService;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.entity.FeedComment;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.req.feed.FeedCommentReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedCommentVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 朋友圈评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentServiceImpl implements FeedCommentService {

    private final FeedCommentDao feedCommentDao;
    private final FeedDao feedDao;
    private final CachePlusOps cachePlusOps;
    private final UserSummaryCache userSummaryCache;
    private final FeedNotifyService feedNotifyService;

    @Override
    @Transactional
    public Boolean addComment(Long uid, FeedCommentReq req) {
        // 1. 校验朋友圈是否存在
        Feed feed = feedDao.getById(req.getFeedId());
        if (Objects.isNull(feed)) {
            throw new BizException("朋友圈不存在");
        }

        // 2. 如果是回复评论，校验被回复的评论是否存在
        if (Objects.nonNull(req.getReplyCommentId())) {
            FeedComment replyComment = feedCommentDao.getById(req.getReplyCommentId());
            if (Objects.isNull(replyComment)) {
                throw new BizException("被回复的评论不存在");
            }
        }

        // 3. 创建评论
        FeedComment feedComment = FeedComment.builder()
                .feedId(req.getFeedId())
                .uid(uid)
                .replyCommentId(req.getReplyCommentId())
                .replyUid(req.getReplyUid())
                .content(req.getContent())
                .build();
        feedCommentDao.save(feedComment);

        // 4. 发送评论通知
        feedNotifyService.notifyFeedComment(feedComment);

        // 5. 清除缓存
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                cachePlusOps.del(FeedCommentCacheKeyBuilder.build(req.getFeedId()));
            }
        });

        return true;
    }

    @Override
    @Transactional
    public Boolean delComment(Long uid, Long commentId) {
        // 1. 查询评论是否存在
        FeedComment comment = feedCommentDao.getById(commentId);
        if (Objects.isNull(comment)) {
            throw new BizException("评论不存在");
        }

        // 2. 校验权限
        if (!comment.getUid().equals(uid)) {
            throw new BizException("无权删除该评论");
        }

        // 3. 逻辑删除评论
        feedCommentDao.removeById(commentId);

        // 4. 清除缓存（在事务提交后执行，确保数据库已更新）
        Long feedId = comment.getFeedId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                cachePlusOps.del(FeedCommentCacheKeyBuilder.build(feedId));
            }
        });

        return true;
    }

    @Override
    public CursorPageBaseResp<FeedCommentVo> getCommentPage(Long feedId, CursorPageBaseReq request) {
        // 1. 分页查询评论
        CursorPageBaseResp<FeedComment> commentPage = feedCommentDao.getCommentPage(feedId, request);

        // 2. 组装用户信息
        List<FeedCommentVo> commentVoList = buildCommentVoList(commentPage.getList());

        // 3. 返回结果
        CursorPageBaseResp<FeedCommentVo> result = new CursorPageBaseResp<>();
        result.setList(commentVoList);
        result.setCursor(commentPage.getCursor());
        result.setIsLast(commentPage.getIsLast());
        return result;
    }

    @Override
    public List<FeedCommentVo> getCommentList(Long feedId, Integer limit) {
        // 1. 从缓存获取评论列表
        CacheHashKey hashKey = FeedCommentCacheKeyBuilder.build(feedId);
        CacheResult<List<FeedComment>> result = cachePlusOps.get(hashKey, t -> feedCommentDao.getCommentListByFeedId(feedId));
        List<FeedComment> commentList = result.getValue();

        if (CollUtil.isEmpty(commentList)) {
            return new ArrayList<>();
        }

        // 2. 限制数量
        if (Objects.nonNull(limit) && commentList.size() > limit) {
            commentList = commentList.subList(0, limit);
        }

        // 3. 组装用户信息
        return buildCommentVoList(commentList);
    }

    @Override
    public Integer getCommentCount(Long feedId) {
        return feedCommentDao.getCommentCount(feedId);
    }

    /**
     * 组装评论VO列表（包含用户信息）
     */
    private List<FeedCommentVo> buildCommentVoList(List<FeedComment> commentList) {
        if (CollUtil.isEmpty(commentList)) {
            return new ArrayList<>();
        }

        // 1. 收集所有需要查询的用户ID
        Set<Long> uidSet = new HashSet<>();
        commentList.forEach(comment -> {
            uidSet.add(comment.getUid());
            if (Objects.nonNull(comment.getReplyUid())) {
                uidSet.add(comment.getReplyUid());
            }
        });

        // 2. 批量获取用户信息
        Map<Long, SummeryInfoDTO> userInfoMap = userSummaryCache.getBatch(new ArrayList<>(uidSet));

        // 3. 组装返回结果
        return commentList.stream()
                .map(comment -> {
                    FeedCommentVo vo = new FeedCommentVo();
                    BeanUtil.copyProperties(comment, vo);

                    // 设置评论人信息
                    SummeryInfoDTO userInfo = userInfoMap.get(comment.getUid());
                    if (Objects.nonNull(userInfo)) {
                        vo.setUserName(userInfo.getName());
                        vo.setUserAvatar(userInfo.getAvatar());
                    }

                    // 设置被回复人信息
                    if (Objects.nonNull(comment.getReplyUid())) {
                        SummeryInfoDTO replyUserInfo = userInfoMap.get(comment.getReplyUid());
                        if (Objects.nonNull(replyUserInfo)) {
                            vo.setReplyUserName(replyUserInfo.getName());
                        }
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }
}
