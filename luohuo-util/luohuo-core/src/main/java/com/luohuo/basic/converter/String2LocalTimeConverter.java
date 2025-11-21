package com.luohuo.basic.converter;

import org.springframework.core.convert.converter.Converter;
import com.luohuo.basic.utils.DateUtils;

import java.time.LocalTime;

/**
 * 解决 @RequestParam LocalTime Date 类型的入参，参数转换问题。
 * <p>
 * HH:mm:ss
 * HH时mm分ss秒
 *
 * @author 乾乾
 * @date 2019-04-30
 */
public class String2LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        return DateUtils.parseAsLocalTime(source);
    }
}
