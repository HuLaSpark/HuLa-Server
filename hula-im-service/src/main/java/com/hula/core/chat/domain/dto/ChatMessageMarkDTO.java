package com.hula.core.chat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息标记请求
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkDTO {

    @Schema(description ="操作者")
    private Long uid;

    @Schema(description ="消息id")
    private Long msgId;

    @Schema(description ="标记类型 1点赞 2举报")
    private Integer markType;

    @Schema(description ="动作类型 1确认 2取消")
    private Integer actType;
}
