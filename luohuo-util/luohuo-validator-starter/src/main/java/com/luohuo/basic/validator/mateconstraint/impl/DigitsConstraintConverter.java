package com.luohuo.basic.validator.mateconstraint.impl;


import jakarta.validation.constraints.Digits;
import com.luohuo.basic.validator.mateconstraint.IConstraintConverter;
import com.luohuo.basic.validator.utils.ValidatorConstants;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 长度 转换器
 *
 * @author zuihou
 * @date 2019-07-25 15:15
 */
public class DigitsConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {

    @Override
    protected List<String> getMethods() {
        return Arrays.asList("integer", "fraction", ValidatorConstants.MESSAGE);
    }

    @Override
    protected String getType(Class<? extends Annotation> type) {
        return type.getSimpleName();
    }

    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return Collections.singletonList(Digits.class);
    }

}
