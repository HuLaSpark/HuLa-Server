package com.luohuo.flex.im.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Date 2025/02/19 10:37
 * @Description 群基础信息
 */
@Data
public class RoomMyInfoReq {

	@NotNull(message = "请选择群聊")
	private Long id;

	@Schema(description ="我在群里面的名称")
	@Size(min = 0, max = 12, message = "我在群里面的名称称长度必须在0到12个字符之间")
	private String myName;

	@Schema(description ="群备注")
	@Size(min = 0, max = 10, message = "群备注长度必须在0到10个字符之间")
	private String remark;
}
