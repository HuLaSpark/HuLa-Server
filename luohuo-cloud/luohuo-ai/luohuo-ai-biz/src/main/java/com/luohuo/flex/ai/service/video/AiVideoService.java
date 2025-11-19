package com.luohuo.flex.ai.service.video;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.video.vo.AiVideoGenerateReqVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoPageReqVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoUpdateReqVO;
import com.luohuo.flex.ai.controller.video.vo.GiteeAiVideoNotifyVO;
import com.luohuo.flex.ai.dal.video.AiVideoDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * AI 视频生成 Service 接口
 *
 * @author 乾乾
 */
public interface AiVideoService {

    /**
     * 获取我的视频分页
     *
     * @param userId     用户编号
     * @param pageReqVO  分页请求参数
     * @return 视频分页结果
     */
    PageResult<AiVideoDO> getVideoPageMy(Long userId, AiVideoPageReqVO pageReqVO);

    /**
     * 获取视频详情
     *
     * @param id 视频编号
     * @return 视频信息
     */
    AiVideoDO getVideo(Long id);

    /**
     * 获取视频列表
     *
     * @param ids 视频编号列表
     * @return 视频列表
     */
    List<AiVideoDO> getVideoList(List<Long> ids);

    /**
     * 生成视频
     *
     * @param userId        用户编号
     * @param generateReqVO 生成请求参数
     * @return 视频编号
     */
    Long generateVideo(Long userId, AiVideoGenerateReqVO generateReqVO);

    /**
     * 删除我的视频
     *
     * @param id     视频编号
     * @param userId 用户编号
     */
    void deleteVideoMy(Long id, Long userId);

    /**
     * 获取视频分页（管理后台）
     *
     * @param pageReqVO 分页请求参数
     * @return 视频分页结果
     */
    PageResult<AiVideoDO> getVideoPage(AiVideoPageReqVO pageReqVO);

    /**
     * 更新视频
     *
     * @param updateReqVO 更新请求参数
     */
    void updateVideo(@Valid AiVideoUpdateReqVO updateReqVO);

    /**
     * 删除视频（管理后台）
     *
     * @param id 视频编号
     */
    void deleteVideo(Long id);

    /**
     * 恢复未完成的视频
     *
     * 用于系统重启后，恢复之前未完成的视频生成任务
     *
     * @return 恢复的视频数量
     */
    Long recoverIncompleteVideos();

    /**
     * 恢复单个视频
     *
     * @param videoId 视频编号
     */
    void recoverVideo(Long videoId);

    /**
     * Gitee AI 视频生成回调通知
     *
     * @param notify 回调通知数据
     */
    void giteeAiNotify(GiteeAiVideoNotifyVO notify);
}
