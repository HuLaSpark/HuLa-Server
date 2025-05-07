package com.hula.core.chat.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * 消息标记请求
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkReq {
    @NotNull
    @Schema(description ="消息id")
    private Long msgId;

	/**
	 * @see com.hula.core.chat.domain.enums.MessageMarkTypeEnum
	 */
    @NotNull
    @Schema(description ="标记类型 1点赞 2举报")
    private Integer markType;

    @NotNull
    @Schema(description ="动作类型 1确认 2取消")
    private Integer actType;
}
