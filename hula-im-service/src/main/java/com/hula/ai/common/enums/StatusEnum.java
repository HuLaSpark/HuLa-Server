package com.hula.ai.common.enums;

import lombok.Getter;

/**
 * 状态枚举
 *
 * @author: 云裂痕
 * @date: 2020/8/6
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Getter
public enum StatusEnum {

    /**
     * 禁用
     */
    DISABLED(0),

    /**
     * 启用
     */
    ENABLED(1);

    private final Integer value;

    StatusEnum(final Integer value) {
        this.value = value;
    }

}
