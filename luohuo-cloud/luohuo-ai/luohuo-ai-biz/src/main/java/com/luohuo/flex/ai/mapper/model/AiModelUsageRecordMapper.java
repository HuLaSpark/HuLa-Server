package com.luohuo.flex.ai.mapper.model;

import com.luohuo.flex.ai.dal.model.AiModelUsageRecordDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import org.springframework.stereotype.Repository;

/**
 * AI 公开模型使用记录 Mapper
 *
 * @author 乾乾
 */
@Repository
public interface AiModelUsageRecordMapper extends BaseMapperX<AiModelUsageRecordDO> {

	/**
	 * 根据用户ID和模型ID查询使用记录
	 *
	 * @param userId 用户ID
	 * @param modelId 模型ID
	 * @return 使用记录
	 */
	default AiModelUsageRecordDO selectByUserIdAndModelId(Long userId, Long modelId) {
		return selectOne(new LambdaQueryWrapperX<AiModelUsageRecordDO>()
				.eq(AiModelUsageRecordDO::getUserId, userId)
				.eq(AiModelUsageRecordDO::getModelId, modelId));
	}
}