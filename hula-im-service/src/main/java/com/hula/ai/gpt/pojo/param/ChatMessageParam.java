package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 聊天摘要对象 参数
 *
 * @author: 云裂痕
 * @date: 2025-03-06
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageParam implements Serializable {

    private static final long serialVersionUID = 1L;

	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;

	@Schema(description = "用户id")
	private Long uid;

	private String chatNumber;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "消息id")
	private String messageId;

	@Schema(description = "模型")
	private String model;

	@Schema(description = "角色")
	private String role;

	@Schema(description = "调用token")
	private String appKey;

	/**
	 * 结束原因
	 */
	private String finishReason;
}
