package com.luohuo.flex.im.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

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
	 * @see MessageMarkTypeEnum
	 */
    @NotNull
    private Integer markType;

    @NotNull
    @Schema(description ="动作类型 1确认 2取消")
    private Integer actType;
}
