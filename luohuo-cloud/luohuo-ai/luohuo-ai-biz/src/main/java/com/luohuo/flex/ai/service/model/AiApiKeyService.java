package com.luohuo.flex.ai.service.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeySaveReqVO;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * AI API 密钥 Service 接口
 *
 */
public interface AiApiKeyService {

    /**
     * 创建 API 密钥
     *
     * @param createReqVO 创建信息
     * @param userId      用户编号
     * @return 编号
     */
    Long createApiKey(@Valid AiApiKeySaveReqVO createReqVO, Long userId);

    /**
     * 更新 API 密钥
     *
     * @param updateReqVO 更新信息
     * @param userId      用户编号
     */
    void updateApiKey(@Valid AiApiKeySaveReqVO updateReqVO, Long userId);

    /**
     * 删除 API 密钥
     *
     * @param id     编号
     * @param userId 用户编号
     */
    void deleteApiKey(Long id, Long userId);

    /**
     * 获得 API 密钥
     *
     * @param id 编号
     * @return API 密钥
     */
    AiApiKeyDO getApiKey(Long id);

    /**
     * 校验 API 密钥
     *
     * @param id 比那好
     * @return API 密钥
     */
    AiApiKeyDO validateApiKey(Long id);

    /**
     * 获得 API 密钥分页
     *
     * @param pageReqVO 分页查询
     * @param userId    用户编号
     * @return API 密钥分页
     */
    PageResult<AiApiKeyDO> getApiKeyPage(AiApiKeyPageReqVO pageReqVO, Long userId);

    /**
     * 获得 API 密钥列表
     *
     * @return API 密钥列表
     */
    List<AiApiKeyDO> getApiKeyList();

    /**
     * 获得默认的 API 密钥
     *
     * @param platform 平台
     * @param status 状态
     * @return API 密钥
     */
    AiApiKeyDO getRequiredDefaultApiKey(String platform, Integer status);

}