package com.hula.core.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 转发时只校验用户是否具备 sourceRoomId 房间的权限; 发送消息时会校验 roomId 的权限
 * 消息实体
 */
@Data
public class MergeMessageReq implements Serializable {

	@Schema(description = "消息来源房间id")
	@NotNull(message = "请选择消息来源房间")
	private Long fromRoomId;

	@Schema(description = "接收的房间ID")
	@NotNull(message = "房间不能为空")
	private Long roomId;

	@Schema(description = "合并消息的子消息列表")
	@NotEmpty(message = "请选择要转发的消息")
	private List<Long> messageIds;
}
