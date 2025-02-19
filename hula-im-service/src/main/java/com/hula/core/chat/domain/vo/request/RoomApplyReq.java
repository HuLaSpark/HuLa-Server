package com.hula.core.chat.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Description: 申请好友信息
 * Date: 2023-03-23
 */
@Data
public class RoomApplyReq {
	@NotBlank
    @Schema(description = "申请信息")
    private String msg;

	@NotNull(message = "请选择群聊")
	@Schema(description = "群聊id")
	private Long targetGroupId;
}
