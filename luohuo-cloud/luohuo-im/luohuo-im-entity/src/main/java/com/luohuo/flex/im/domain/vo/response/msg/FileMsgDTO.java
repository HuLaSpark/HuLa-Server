package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.im.domain.entity.msg.ReplyMsg;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 语音消息入参
 * @author nyh
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileMsgDTO extends BaseFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description ="文件名（带后缀）")
    @NotBlank
    private String fileName;

	@Schema(description ="回复的消息id")
	private Long replyMsgId;

	@Schema(description ="艾特的uid")
	private List<Long> atUidList;

	@Schema(description ="父消息，如果没有父消息，返回的是null")
	private ReplyMsg reply;
}
