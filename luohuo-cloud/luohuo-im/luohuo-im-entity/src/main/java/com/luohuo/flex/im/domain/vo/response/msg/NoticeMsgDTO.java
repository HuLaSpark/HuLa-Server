package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.im.domain.entity.msg.ReplyMsg;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公告消息入参
 * @author 乾乾
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeMsgDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	@Schema(description ="公告id")
	private Long id;

	@Schema(description = "房间id")
	private Long roomId;

	@Schema(description = "公告发布人")
	private Long uid;

	@Schema(description ="公告内容")
    private String content;

	@Schema(description = "发布时间")
	private Long createTime;

	@Schema(description = "置顶")
	private Boolean top;

	@Schema(description ="回复的消息id")
	private Long replyMsgId;

	@Schema(description ="父消息，如果没有父消息，返回的是null")
	private ReplyMsg reply;
}
