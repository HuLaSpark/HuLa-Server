package com.luohuo.basic.utils;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * 默认值
 *
 * @author 乾乾
 * @date 2020/9/25 2:07 下午
 */
public final class DefValueHelper {
    private DefValueHelper() {
    }

    public static String getOrDef(String val, String def) {
        return StrUtil.isEmpty(val) ? def : val;
    }

    public static <T extends Serializable> T getOrDef(T val, T def) {
        return val == null ? def : val;
    }

}
