package com.luohuo.flex.im.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@Size(min = 1, max = 10, message = "群名称长度必须在1到10个字符之间")
	@Schema(description ="群名称")
	private String name;

	@NotEmpty(message = "群头像不能为空")
	@Schema(description ="群头像")
	private String avatar;

	@Schema(description ="是否允许扫码直接进群")
	private Boolean allowScanEnter;
}
