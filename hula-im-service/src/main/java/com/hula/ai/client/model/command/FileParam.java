package com.hula.ai.client.model.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FileParam {

	@NotBlank(message = "请选择解析模型")
	private String model;
}
