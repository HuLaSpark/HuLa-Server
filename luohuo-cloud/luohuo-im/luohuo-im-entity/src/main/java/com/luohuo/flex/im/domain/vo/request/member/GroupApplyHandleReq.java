package com.luohuo.flex.im.domain.vo.request.member;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupApplyHandleReq {
    @NotNull(message = "申请ID不能为空")
    private Long applyId;
    
    @NotNull(message = "审批状态不能为空 0未处理 1-已同意 2拒绝 3忽略")
	@Min(value = 0, message = "状态值不能小于0")
	@Max(value = 3, message = "状态值不能大于3")
    private Integer status;
}