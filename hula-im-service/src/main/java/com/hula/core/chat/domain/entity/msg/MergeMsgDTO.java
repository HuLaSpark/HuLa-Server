package com.hula.core.chat.domain.entity.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 合并消息入参
 * @author 乾乾
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MergeMsgDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	@Schema(description ="合并的内容")
    private List<MergeMsg> messages;

	@Schema(description ="回复的消息id")
	private Long replyMsgId;

	@Schema(description ="父消息，如果没有父消息，返回的是null")
	private ReplyMsg reply;
}
