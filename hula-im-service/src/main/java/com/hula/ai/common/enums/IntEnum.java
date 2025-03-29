package com.hula.ai.common.enums;

import lombok.Getter;

/**
 * 数字枚举
 *
 * @author: 云裂痕
 * @date: 2020/10/21
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Getter
public enum IntEnum {

    /**
     * 数值
     */
    MINUS_ONE(-1),

    ZERO(0),

    ONE(1),

    TWO(2),

    THREE(3),

    FOUR(4),

    FIVE(5),

    SIX(6),

    SEVEN(7),

    EIGHT(8),

    NINE(9),

    TEN(10),

    ELEVEN(11),

    TWELVE(12),

    THIRTEEN(13),

    FOURTEEN(14),

    FIFTEEN(15),

    SIXTEEN(16),

    SEVENTEEN(17),

    EIGHTEEN(18),

    NINETEEN(19),

    TWENTY(20);

    private final int value;

    IntEnum(final int value) {
        this.value = value;
    }

}
