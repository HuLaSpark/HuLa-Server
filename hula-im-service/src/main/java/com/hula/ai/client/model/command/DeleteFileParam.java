package com.hula.ai.client.model.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class DeleteFileParam extends FileParam {

	@NotBlank(message = "请选择文件")
	private List<String> fileIds;
}
