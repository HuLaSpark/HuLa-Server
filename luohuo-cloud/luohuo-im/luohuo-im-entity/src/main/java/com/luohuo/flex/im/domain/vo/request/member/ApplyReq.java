package com.luohuo.flex.im.domain.vo.request.member;

import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
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

	/**
	 * @see NoticeStatusEnum
	 */
	@NotNull(message = "请选择审批类型")
	private Integer state;
}
