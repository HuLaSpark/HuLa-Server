package com.luohuo.flex.ai.dal.model;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.flex.ai.dal.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AI 公开模型使用记录 DO
 *
 * <p>记录用户对公开模型的使用次数，用于限制每个用户对公开模型的使用次数</p>
 *
 * @author 乾乾
 */
@TableName("ai_model_usage_record")
@KeySequence("ai_model_usage_record_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AiModelUsageRecordDO extends BaseDO {

	/**
	 * 编号
	 */
	@TableId
	private Long id;

	/**
	 * 用户编号
	 *
	 * 关联 AdminUserDO 的 userId 字段
	 */
	private Long userId;

	/**
	 * 模型编号
	 *
	 * 关联 {@link AiModelDO#getId()}
	 */
	private Long modelId;

	/**
	 * 使用次数
	 */
	private Integer usageCount;

	/**
	 * 剩余次数
	 */
	private Integer remainingCount;

}
