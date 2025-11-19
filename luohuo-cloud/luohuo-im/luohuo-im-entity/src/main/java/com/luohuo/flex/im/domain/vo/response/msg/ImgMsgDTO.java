package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.im.domain.entity.msg.ReplyMsg;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 图片消息入参
 * @author nyh
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImgMsgDTO extends BaseFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description ="宽度（像素）")
    @NotNull
    private Integer width;

    @Schema(description ="高度（像素）")
    @NotNull
    private Integer height;

	@Schema(description ="回复的消息id")
	private Long replyMsgId;

	@Schema(description ="艾特的uid")
	private List<Long> atUidList;

	@Schema(description ="父消息，如果没有父消息，返回的是null")
	private ReplyMsg reply;
}