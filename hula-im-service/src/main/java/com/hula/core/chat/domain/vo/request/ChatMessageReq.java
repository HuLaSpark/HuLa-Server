package com.hula.core.chat.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * 聊天信息点播
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReq {
    @NotNull
    @Schema(description ="房间id")
    private Long roomId;

    @Schema(description ="消息类型")
    @NotNull
    private Integer msgType;

    @Schema(description ="消息内容，类型不同传值不同")
    @NotNull
    private Object body;

	@Schema(description ="跳过消息校验")
	private Boolean skip = false;
}
