package com.luohuo.flex.im.domain.vo.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进群审批
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyReq {
    @NotNull(message = "请选择邀请记录")
    @Schema(description ="邀请的id")
    private Long applyId;

	@NotNull(message = "请选择邀请记录")
	@Schema(description ="1 = 同意 2 = 拒绝")
	private Integer state;
}
