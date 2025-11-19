package com.luohuo.flex.ai.controller.chat.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "会话id")
@Data
public class AiDelReqVO {

    @Schema(description = "会话id", example = "1")
    private List<Long> conversationIdList;

}
