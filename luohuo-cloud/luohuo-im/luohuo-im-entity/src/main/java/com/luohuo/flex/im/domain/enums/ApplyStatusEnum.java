package com.luohuo.flex.im.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nyh
 * 申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum ApplyStatusEnum {

    WAIT_APPROVAL(1, "待审批"),

    AGREE(2, "同意"),

    REJECT(3, "拒绝"),

    IGNORE(4, "忽略");
    private final Integer code;

    private final String desc;
}
