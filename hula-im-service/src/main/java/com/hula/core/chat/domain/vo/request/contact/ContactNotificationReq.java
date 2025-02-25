package com.hula.core.chat.domain.vo.request.contact;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Description: 会话屏蔽
 * Date: 2025-02-25 17:20:00
 */
@Builder
@Data
public class ContactNotificationReq {

    @NotNull(message = "请选择会话")
    @Schema(description ="房间id")
    private Long roomId;

	@NotNull(message = "请选择通知类型")
	@Schema(description ="通知类型 0 -> 允许接受消息 1 -> 接收但不提醒[免打扰] 2 -> 屏蔽消息")
	private Integer type;

	@Schema(description = "[type = 2 才需要传] true -> 屏蔽 false -> 正常")
	private Boolean deFriend;
}
