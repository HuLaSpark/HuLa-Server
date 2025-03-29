package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * order接口参数
 */
@Data
public class OrderParam {
	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;
	private LocalDateTime successTime;
	private String tradeNo;
	private String transactionId;
	private Long uid;
	private Long combId;
	private Integer chanel;
}