package com.luohuo.flex.im.domain.vo.request.contact;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Description: 会话置顶
 * Date: 2025-02-19
 */
@Builder
@Data
public class ContactTopReq {

    @NotNull(message = "请选择会话")
    @Schema(description ="房间id")
    private Long roomId;

	@Schema(description ="置顶类型")
	private Boolean top;
}
