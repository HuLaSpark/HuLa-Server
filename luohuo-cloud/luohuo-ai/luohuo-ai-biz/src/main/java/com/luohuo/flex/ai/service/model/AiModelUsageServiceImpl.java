package com.luohuo.flex.ai.service.model;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.dal.model.AiModelUsageRecordDO;
import com.luohuo.flex.ai.mapper.model.AiModelUsageRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.MODEL_USAGE_LIMIT_EXCEEDED;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

/**
 * AI 模型使用次数 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Slf4j
public class AiModelUsageServiceImpl implements AiModelUsageService {

	@Resource
	private AiModelUsageRecordMapper usageRecordMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void checkAndDeductUsage(Long userId, AiModelDO model) {
		// 1. 如果是私有模型（publicStatus=1），不限制使用次数
		if (Integer.valueOf(1).equals(model.getPublicStatus())) {
			return;
		}

		// 2. 如果是模型创建者，不限制使用次数
		if (ObjUtil.equal(model.getUserId(), userId)) {
			return;
		}

		// 3. 公开模型：检查并扣减使用次数
		AiModelUsageRecordDO record = usageRecordMapper.selectByUserIdAndModelId(userId, model.getId());

		if (record == null) {
			// 3.1 首次使用，创建使用记录
			record = AiModelUsageRecordDO.builder()
					.userId(userId)
					.modelId(model.getId())
					.usageCount(1)
					.remainingCount(PUBLIC_MODEL_USAGE_LIMIT - 1)
					.build();
			usageRecordMapper.insert(record);
			log.info("[checkAndDeductUsage] 首次使用公开模型，modelId={}, userId={}, remainingCount={}", model.getId(), userId, record.getRemainingCount());
		} else {
			// 3.2 检查剩余次数
			if (record.getRemainingCount() <= 0) {
				log.warn("[checkAndDeductUsage] 公开模型使用次数已用完，modelId={}, userId={}", model.getId(), userId);
				throw exception(MODEL_USAGE_LIMIT_EXCEEDED);
			}

			// 3.3 扣减使用次数
			record.setUsageCount(record.getUsageCount() + 1);
			record.setRemainingCount(record.getRemainingCount() - 1);
			usageRecordMapper.updateById(record);
			log.info("[checkAndDeductUsage] 扣减公开模型使用次数，modelId={}, userId={}, usageCount={}, remainingCount={}", model.getId(), userId, record.getUsageCount(), record.getRemainingCount());
		}
	}

	@Override
	public int getRemainingUsageCount(Long userId, Long modelId) {
		// 2. 查询使用记录
		AiModelUsageRecordDO record = usageRecordMapper.selectByUserIdAndModelId(userId, modelId);

		if (record == null) {
			// 未使用过，返回初始限制次数
			return PUBLIC_MODEL_USAGE_LIMIT;
		}

		return record.getRemainingCount();
	}

}
