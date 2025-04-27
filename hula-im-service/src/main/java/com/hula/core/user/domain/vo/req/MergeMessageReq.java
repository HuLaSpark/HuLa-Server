package com.hula.core.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 消息实体
 */
@Data
public class MergeMessageReq implements Serializable {

	@Schema(description = "接收的房间ID")
	@NotNull(message = "房间不能为空")
	private Long roomId;

	@Schema(description = "合并消息的子消息列表")
	@NotEmpty(message = "请选择要转发的消息")
	private List<Long> messageIds;
}
