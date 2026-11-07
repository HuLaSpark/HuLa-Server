package com.luohuo.flex.im.domain.vo.request.contact;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 会话删除/会话显示
 * Date: 2025-03-03
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactHideReq {

	@NotNull(message = "请选择房间")
    @Schema(description ="房间id")
    private Long roomId;

	@NotNull(message = "请选择操作")
	@Schema(description ="true -> 隐藏 false -> 隐藏")
	private Boolean hide;
}
