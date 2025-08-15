package com.luohuo.flex.im.domain.vo.request.member;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupApplyHandleReq {
    @NotNull(message = "申请ID不能为空")
    private Long applyId;
    
    @NotNull(message = "审批状态不能为空 2=同意 3=拒绝")
    private Integer status;
}