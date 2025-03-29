package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * openKey接口参数
 */
@Data
public class OpenKeyParam {
	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;
	private String name;
	private String model;
	private Integer status;
	private String appKey;
}