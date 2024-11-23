package com.hula.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 幂等类型
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum LoginTypeEnum {
    PC("pc", "PC端"),
    MOBILE("mobile", "移动端"),
    ;

    private final String type;
    private final String desc;

}
