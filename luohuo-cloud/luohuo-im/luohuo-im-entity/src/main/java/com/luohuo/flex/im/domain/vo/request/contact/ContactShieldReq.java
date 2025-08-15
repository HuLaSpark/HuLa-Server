package com.luohuo.flex.im.domain.vo.request.contact;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Description: 会话屏蔽
 * Date: 2025-03-12 17:20:00
 */
@Builder
@Data
public class ContactShieldReq {

    @NotNull(message = "请选择会话")
    @Schema(description ="房间id")
    private Long roomId;

	@NotNull(message = "请选择屏蔽状态")
	@Schema(description = "true -> 屏蔽 false -> 正常")
	private Boolean state;
}
