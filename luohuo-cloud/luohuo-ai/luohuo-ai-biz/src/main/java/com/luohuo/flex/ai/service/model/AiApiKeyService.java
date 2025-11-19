package com.luohuo.flex.ai.service.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyBalanceRespVO;
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
     * 获得 API 密钥列表（包含系统公开密钥和用户私有密钥）
     *
     * @param userId 用户编号
     * @return API 密钥列表
     */
    List<AiApiKeyDO> getApiKeyList(Long userId);

    /**
     * 获得所有 API 密钥列表（后台管理专用）
     *
     * @return API 密钥列表
     */
    List<AiApiKeyDO> getAllApiKeyList();

    /**
     * 获得所有 API 密钥分页（后台管理专用，无权限限制）
     *
     * @param pageReqVO 分页查询
     * @return API 密钥分页
     */
    PageResult<AiApiKeyDO> getAdminApiKeyPage(AiApiKeyPageReqVO pageReqVO);

    /**
     * 获得默认的 API 密钥
     *
     * @param platform 平台
     * @param status 状态
     * @return API 密钥
     */
    AiApiKeyDO getRequiredDefaultApiKey(String platform, Integer status);

    /**
     * 查询 API 密钥余额
     *
     * @param id     API 密钥编号
     * @param userId 用户编号
     * @return 余额信息
     */
    AiApiKeyBalanceRespVO getApiKeyBalance(Long id, Long userId);

    /**
     * 管理员更新 API 密钥
     *
     * @param updateReqVO 更新信息
     */
    void updateApiKeyAdmin(@Valid AiApiKeySaveReqVO updateReqVO);

    /**
     * 管理员删除 API 密钥
     *
     * @param id 编号
     */
    void deleteApiKeyAdmin(Long id);

}