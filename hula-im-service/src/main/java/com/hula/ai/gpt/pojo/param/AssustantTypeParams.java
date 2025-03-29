package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AssustantTypeParams {
	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;

	private String name;
	private Integer status;
}