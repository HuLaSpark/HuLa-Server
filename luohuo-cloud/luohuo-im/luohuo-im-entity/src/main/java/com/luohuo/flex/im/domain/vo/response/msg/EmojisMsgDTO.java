package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.im.domain.entity.msg.ReplyMsg;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 表情图片消息入参
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmojisMsgDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description ="下载地址")
    @NotBlank
    private String url;

	@Schema(description ="回复的消息id")
	private Long replyMsgId;

	@Schema(description ="艾特的uid")
	private List<Long> atUidList;

	@Schema(description ="父消息，如果没有父消息，返回的是null")
	private ReplyMsg reply;
}