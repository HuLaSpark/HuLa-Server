package com.luohuo.flex.ai.service.model;


import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelSaveMyReqVO;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelSaveReqVO;
import com.luohuo.flex.ai.core.model.MidjourneyApi;
import com.luohuo.flex.ai.core.model.SunoApi;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.exception.ServiceException;
import dev.tinyflow.core.Tinyflow;
import jakarta.validation.Valid;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.VectorStore;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * AI 模型 Service 接口
 *
 * @author 乾乾
 * @since 2024/4/24 19:42
 */
public interface AiModelService {

    /**
     * 创建模型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createModel(@Valid AiModelSaveReqVO createReqVO);

    /**
     * 创建【我的】模型
     *
     * @param createReqVO 创建信息
     * @param userId      用户编号
     * @return 编号
     */
    Long createModelMy(@Valid AiModelSaveMyReqVO createReqVO, Long userId);

    /**
     * 更新【我的】模型
     *
     * @param updateReqVO 更新信息
     * @param userId      用户编号
     */
    void updateModelMy(@Valid AiModelSaveMyReqVO updateReqVO, Long userId);

    /**
     * 删除模型
     *
     * @param id 编号
     */
    void deleteModel(Long id);

    /**
     * 删除【我的】模型
     *
     * @param id     编号
     * @param userId 用户编号
     */
    void deleteModelMy(Long id, Long userId);

    /**
     * 获得模型
     *
     * @param id 编号
     * @return 模型
     */
    AiModelDO getModel(Long id);

    /**
     * 获得默认的模型
     *
     * 如果获取不到，则抛出 {@link ServiceException} 业务异常
     *
     * @return 模型
     */
    AiModelDO getRequiredDefaultModel(Integer type);

    /**
     * 获得模型分页
     *
     * @param pageReqVO 分页查询
     * @return 模型分页
     */
    PageResult<AiModelDO> getModelPage(AiModelPageReqVO pageReqVO);

    /**
     * 获得【我的】模型分页
     *
     * @param pageReqVO 分页查询
     * @param userId    用户编号
     * @return 模型分页
     */
    PageResult<AiModelDO> getModelMyPage(AiModelPageReqVO pageReqVO, Long userId);

    /**
     * 校验模型是否可使用
     *
     * @param id 编号
     * @return 模型
     */
    AiModelDO validateModel(Long id);

    /**
     * 获得模型列表
     *
     * @param status 状态
     * @param type 类型
     * @param platform 平台，允许空
     * @return 模型列表
     */
    List<AiModelDO> getModelListByStatusAndType(Integer status, Integer type,
                                                @Nullable String platform);

    /**
     * 获得模型列表（用户可见）
     *
     * 包含：系统级别的公开模型 + 用户自己的私有模型
     *
     * @param status   状态
     * @param type     类型
     * @param platform 平台，允许空
     * @param userId   用户编号
     * @return 模型列表
     */
    List<AiModelDO> getModelListByStatusAndTypeAndUserId(Integer status, Integer type,
                                                         @Nullable String platform, Long userId);

    /**
     * 获得所有模型列表（后台管理专用）
     *
     * @return 模型列表
     */
    List<AiModelDO> getAllModelList();

    /**
     * 管理员更新模型
     *
     * @param updateReqVO 更新信息
     */
    void updateModelAdmin(@Valid AiModelSaveReqVO updateReqVO);

    /**
     * 管理员删除模型
     *
     * @param id 编号
     */
    void deleteModelAdmin(Long id);

    // ========== 与 Spring AI 集成 ==========

    /**
     * 获得 ChatModel 对象
     *
     * @param id 编号
     * @return ChatModel 对象
     */
    ChatModel getChatModel(Long id);

    /**
     * 获得 ImageModel 对象
     *
     * @param id 编号
     * @return ImageModel 对象
     */
    ImageModel getImageModel(Long id);

    /**
     * 获得 VideoModel 对象
     *
     * @param id 编号
     * @return VideoModel 对象
     */
    com.luohuo.flex.ai.core.model.video.VideoModel getVideoModel(Long id);

    /**
     * 获得 AudioModel 对象
     *
     * @param id 编号
     * @return AudioModel 对象
     */
    com.luohuo.flex.ai.core.model.audio.AudioModel getAudioModel(Long id);

    /**
     * 获得 MidjourneyApi 对象
     *
     * @param id 编号
     * @return MidjourneyApi 对象
     */
    MidjourneyApi getMidjourneyApi(Long id);

    /**
     * 获得 SunoApi 对象
     *
     * @return SunoApi 对象
     */
    SunoApi getSunoApi();

    /**
     * 获得 VectorStore 对象
     *
     * @param id 编号
     * @param metadataFields 元数据的定义
     * @return VectorStore 对象
     */
    VectorStore getOrCreateVectorStore(Long id, Map<String, Class<?>> metadataFields);

    /**
     * 获取 TinyFlow 所需 LLm Provider
     *
     * @param tinyflow tinyflow
     * @param modelId AI 模型 ID
     */
    void getLLmProvider4Tinyflow(Tinyflow tinyflow, Long modelId);

}
