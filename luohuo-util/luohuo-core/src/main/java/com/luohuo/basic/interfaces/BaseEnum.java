package com.luohuo.basic.interfaces;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 枚举类型基类
 *
 * @author 乾乾
 * @date 2019/07/26
 */
public interface BaseEnum extends IEnum<String> {

    /**
     * 枚举的 name
     *
     * @return 枚举的 name
     */
    default String getCode() {
        return toString();
    }

    /**
     * 前端显示的值
     *
     * @return 描述
     */
    String getDesc();

    /**
     * 扩展参数
     *
     * @return 扩展参数
     */
    default String getExtra() {
        return "";
    }

    /**
     * 判断val是否跟当前枚举相等
     *
     * @param val 值
     * @return 是否等于
     */
    default boolean eq(String val) {
        return this.getCode().equalsIgnoreCase(val);
    }

    /**
     * 枚举值
     *
     * @return 数据库中的值
     */
    @Override
    default String getValue() {
        return getCode();
    }
}
