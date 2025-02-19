package com.hula.core.chat.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Date 2025/02/19 10:37
 * @Description 群基础信息
 */
@Data
public class RoomInfoReq {

	@NotNull(message = "请选择群聊")
	private Long id;

	@NotEmpty(message = "群名称不能为空")
	@Schema(description ="群名称")
	private String name;

	@NotEmpty(message = "群头像不能为空")
	@Schema(description ="群头像")
	private String avatar;
}
