package com.hula.ai.client.model.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CompletionsParam {

	@Schema(description = "文件映射", required = true)
	private List<String> fileIds;

	private String ws;

	@Schema(description = "会话id [每次发送消息都不一样]", required = true)
	private String conversationId;
}
