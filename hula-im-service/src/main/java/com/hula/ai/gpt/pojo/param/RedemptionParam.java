package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * redemption接口参数
 */
@Data
public class RedemptionParam {
	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;
	private String code;
	private Long uid;

	private Long combId;
	private Integer chanel;
}