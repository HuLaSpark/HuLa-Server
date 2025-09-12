package com.luohuo.flex.im.domain.entity.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyMsg implements Serializable {
	@Schema(description = "消息id")
	private String id;
	@Schema(description = "用户uid")
	private String uid;
	@Schema(description = "用户名称")
	private String username;
	@Schema(description = "消息类型 1正常文本 2.撤回消息")
	private Integer type;
	@Schema(description = "消息内容不同的消息类型，见父消息内容体")
	private Object body;
	@Schema(description = "是否可消息跳转 0否 1是")
	private Integer canCallback;
	@Schema(description = "跳转间隔的消息条数")
	private Integer gapCount;
}