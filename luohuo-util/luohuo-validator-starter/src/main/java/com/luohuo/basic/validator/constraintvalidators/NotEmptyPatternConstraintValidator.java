package com.luohuo.basic.validator.constraintvalidators;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.messageinterpolation.util.InterpolationHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import com.luohuo.basic.annotation.constraints.NotEmptyPattern;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * NotEmptyPattern注解的 约束验证器
 *
 * @author 乾乾
 * @date 2021/3/30 7:49 下午
 */
public class NotEmptyPatternConstraintValidator implements ConstraintValidator<NotEmptyPattern, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private java.util.regex.Pattern pattern;
    private String escapedRegexp;

    @Override
    public void initialize(NotEmptyPattern parameters) {
        NotEmptyPattern.Flag[] flags = parameters.flags();
        int intFlag = 0;
        for (NotEmptyPattern.Flag flag : flags) {
            intFlag |= flag.getValue();
        }

        try {
            pattern = java.util.regex.Pattern.compile(parameters.regexp(), intFlag);
        } catch (PatternSyntaxException e) {
            throw LOG.getInvalidRegularExpressionException(e);
        }

        escapedRegexp = InterpolationHelper.escapeMessageParameter(parameters.regexp());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return true;
        }

        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("regexp", escapedRegexp);
        }

        Matcher m = pattern.matcher(value);
        return m.matches();
    }
}
