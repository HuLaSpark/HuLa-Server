package com.luohuo.flex.im.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 乾乾
 * 申请类型枚举
 */
@Getter
@AllArgsConstructor
public enum ApplyEnum {

	USER_FRIEND(1, "好友申请"),

    GROUP(2, "群聊邀请");
    private final Integer code;

    private final String desc;
}
