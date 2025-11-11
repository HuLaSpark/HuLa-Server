package com.luohuo.flex.ai.service.audio;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioGenerateReqVO;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioPageReqVO;
import com.luohuo.flex.ai.dal.audio.AiAudioDO;

import java.util.List;

/**
 * AI 音频生成 Service 接口
 *
 * @author 乾乾
 */
public interface AiAudioService {

    /**
     * 获取我的音频分页
     *
     * @param userId     用户编号
     * @param pageReqVO  分页请求参数
     * @return 音频分页结果
     */
    PageResult<AiAudioDO> getAudioPageMy(Long userId, AiAudioPageReqVO pageReqVO);

    /**
     * 获取音频详情
     *
     * @param id 音频编号
     * @return 音频信息
     */
    AiAudioDO getAudio(Long id);

    /**
     * 获取音频列表
     *
     * @param ids 音频编号列表
     * @return 音频列表
     */
    List<AiAudioDO> getAudioList(List<Long> ids);

    /**
     * 生成音频
     *
     * @param userId        用户编号
     * @param generateReqVO 生成请求参数
     * @return 音频编号
     */
    Long generateAudio(Long userId, AiAudioGenerateReqVO generateReqVO);

    /**
     * 删除我的音频
     *
     * @param id     音频编号
     * @param userId 用户编号
     */
    void deleteAudioMy(Long id, Long userId);
}
