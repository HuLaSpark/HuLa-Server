package com.hula.ai.common.converter;

import com.github.dozermapper.core.DozerConverter;

import java.math.BigDecimal;

/**
 * Double数据类型转BigDecimal
 *
 * @author: 云裂痕
 * @date: 2023/1/15
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class DoubleToBigDecimalConverter extends DozerConverter<Double, BigDecimal> {
    public DoubleToBigDecimalConverter() {
        super(Double.class, BigDecimal.class);
    }

    @Override
    public BigDecimal convertTo(Double a1, BigDecimal a2) {
        a1 = a1 == null ? 0.0 : a1;
        return new BigDecimal(a1);
    }

    @Override
    public Double convertFrom(BigDecimal a1, Double a2) {
        return null;
    }
}
