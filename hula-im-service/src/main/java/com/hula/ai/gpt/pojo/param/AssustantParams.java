package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AssustantParams {
	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;

	private Integer typeId;
	private String mainModel;
	private String title;
	private String name;
	private String model;
	private Integer status;
}