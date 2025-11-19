package com.luohuo.flex.ai.service.platform;

import com.luohuo.flex.ai.dal.platform.AiPlatformDO;

import java.util.List;

/**
 * AI 平台配置 Service 接口
 *
 * @author 乾乾
 */
public interface AiPlatformService {

    /**
     * 获得所有启用的平台列表
     *
     * @return 平台列表
     */
    List<AiPlatformDO> getPlatformList();

    /**
     * 添加模型到平台的示例列表
     *
     * @param platform 平台代码
     * @param model 模型标志
     */
    void addModelToExamples(String platform, String model);

    /**
     * 清理所有平台的重复模型
     *
     * @return 清理的平台数量
     */
    int cleanDuplicateModels();

}
