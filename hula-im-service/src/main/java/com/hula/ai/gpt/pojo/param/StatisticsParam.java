package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 聊天摘要对象 VO
 *
 * @author: 云裂痕
 * @date: 2025-03-06
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsParam implements Serializable {

    private static final long serialVersionUID = 1L;

	@Schema(description = "开始时间")
    private String startDate;

	@Schema(description = "结束时间")
	private String endDate;

}
