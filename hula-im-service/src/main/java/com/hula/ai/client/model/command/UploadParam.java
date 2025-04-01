package com.hula.ai.client.model.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UploadParam {

	@NotBlank(message = "缺少对话标识")
	private String chatNumber;

	@NotBlank(message = "请选择解析模型")
	private String model;
}
