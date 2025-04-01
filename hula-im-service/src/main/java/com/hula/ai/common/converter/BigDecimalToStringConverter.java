package com.hula.ai.common.converter;

import com.github.dozermapper.core.DozerConverter;
import com.hula.ai.common.constant.StringPoolConstant;

import java.math.BigDecimal;

/**
 * BigDecimal数据类型转String
 *
 * @author: 云裂痕
 * @date: 2023/1/15
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class BigDecimalToStringConverter extends DozerConverter<BigDecimal, String> {

    public BigDecimalToStringConverter() {
        super(BigDecimal.class, String.class);
    }

    @Override
    public String convertTo(BigDecimal a1, String a2) {
        return a1 == null ? StringPoolConstant.ZERO : a1.toPlainString();
    }

    @Override
    public BigDecimal convertFrom(String a1, BigDecimal a2) {

        return null;
    }


}
