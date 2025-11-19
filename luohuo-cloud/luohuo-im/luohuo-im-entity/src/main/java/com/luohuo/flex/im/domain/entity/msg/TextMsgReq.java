package com.luohuo.flex.im.domain.entity.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文本消息入参
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgReq {

    @NotBlank(message = "内容不能为空")
    @Size(max = 1024, message = "消息内容过长")
    @Schema(description ="消息内容")
    private String content;

    @Schema(description ="回复的消息id,如果没有别传就好")
    private Long replyMsgId;

    @Schema(description ="艾特的uid")
    @Size(max = 10, message = "一次别艾特这么多人")
    private List<Long> atUidList;
}
