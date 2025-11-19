package com.luohuo.flex.im.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

	@Schema(description = "转发类型")
	@NotNull(message = "转发类型不能为空")
	private Integer type;

	@Schema(description = "接收的房间ID")
	@Size(max = 100, message = "最多只能转发100条消息")
	@NotEmpty(message = "请选择接收消息的房间")
	private List<Long> roomIds;

	@Schema(description = "合并消息的子消息列表")
	@NotEmpty(message = "请选择要转发的消息")
	private List<Long> messageIds;
}
