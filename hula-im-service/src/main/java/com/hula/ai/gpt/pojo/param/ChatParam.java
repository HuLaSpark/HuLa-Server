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
public class ChatParam implements Serializable {

    private static final long serialVersionUID = 1L;

	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;

	private String title;

	@Schema(description = "chat的id")
	private Long chatId;

	private String chatNumber;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "用户名称")
	private String userName;

	@Schema(description = "角色id")
	private Long assistantId;

	@Schema(description = "用户id")
    private Long uid;

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

	public Integer getOffset() {
		if (current == null || current < 1) {
			current = 1; // 默认第一页
		}
		if (size == null || size < 1) {
			size = 10; // 默认每页10条
		}
		return (current - 1) * size;
	}
}
