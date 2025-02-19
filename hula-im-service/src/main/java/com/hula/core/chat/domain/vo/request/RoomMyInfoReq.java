package com.hula.core.chat.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
	private String myName;

	@Schema(description ="群备注")
	private String remark;
}
