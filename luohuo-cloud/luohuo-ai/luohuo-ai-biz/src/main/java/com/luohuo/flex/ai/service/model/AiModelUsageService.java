package com.luohuo.flex.ai.service.model;

import com.luohuo.flex.ai.dal.model.AiModelDO;

/**
 * AI 模型使用次数 Service 接口
 *
 * @author 乾乾
 */
public interface AiModelUsageService {

	/**
	 * 公开模型每个用户的使用次数限制
	 */
	int PUBLIC_MODEL_USAGE_LIMIT = 10;

	/**
	 * 检查并扣减模型使用次数
	 *
	 * <p>规则：</p>
	 * <ul>
	 *   <li>私有模型：无限使用，不扣减次数</li>
	 *   <li>公开模型：每个用户限制使用 10 次</li>
	 * </ul>
	 *
	 * @param userId 用户ID
	 * @param model 模型信息
	 * @throws com.luohuo.flex.ai.exception.ServiceException 如果使用次数已用完
	 */
	void checkAndDeductUsage(Long userId, AiModelDO model);

	/**
	 * 获取用户对指定公开模型的剩余使用次数
	 *
	 * @param userId 用户ID
	 * @param modelId 模型ID
	 * @return 剩余使用次数，如果是私有模型或模型创建者返回 -1 表示无限
	 */
	int getRemainingUsageCount(Long userId, Long modelId);

}