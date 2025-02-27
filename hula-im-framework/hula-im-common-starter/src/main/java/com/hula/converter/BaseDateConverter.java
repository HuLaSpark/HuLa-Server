package com.hula.converter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 解决入参为 Date类型
 *
 * @author qianqian
 * @date 2019-04-30
 */
public abstract class BaseDateConverter<T> {

    /**
     * 值转换
     *
     * @param source   源数据
     * @param function 回调
     * @return 转换后的数据
     */
    public T convert(String source, Function<String, T> function) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = getFormat().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (sourceTrim.matches(entry.getValue())) {
                return function.apply(entry.getKey());
            }
        }
        throw new IllegalArgumentException("无效的日期参数格式:'" + sourceTrim + "'");
    }

    /**
     * 获取子类 具体的格式化表达式
     *
     * @return 格式化
     */
    protected abstract Map<String, String> getFormat();
}
