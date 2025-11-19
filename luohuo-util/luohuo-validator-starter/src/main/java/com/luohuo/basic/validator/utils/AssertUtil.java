package com.luohuo.basic.validator.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.DefaultPropertyNodeNameProvider;
import org.hibernate.validator.internal.properties.DefaultGetterPropertySelectionStrategy;
import org.hibernate.validator.internal.properties.javabean.JavaBeanHelper;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;
import org.hibernate.validator.spi.properties.GetterPropertySelectionStrategy;
import com.luohuo.basic.annotation.constraints.NotEmptyPattern;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.code.GroupErrorEnum;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.validator.constraintvalidators.LengthConstraintValidator;
import com.luohuo.basic.validator.constraintvalidators.NotEmptyConstraintValidator;
import com.luohuo.basic.validator.constraintvalidators.NotEmptyPatternConstraintValidator;
import com.luohuo.basic.validator.constraintvalidators.NotNullConstraintValidator;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * HibernateValidate 校验工具类
 *
 * @since 2024年06月24日14:29:43
 * @author tangyh
 */
@Slf4j
public class AssertUtil {

    private final static Validator VALIDATOR_FAST = warp(Validation.byProvider(HibernateValidator.class).configure().failFast(true)).buildValidatorFactory().getValidator();
    private final static Validator VALIDATOR_ALL = warp(Validation.byProvider(HibernateValidator.class).configure().failFast(false)).buildValidatorFactory().getValidator();

	/**
	 * 全部校验
	 */
	private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	private static boolean isEmpty(Object obj) {
		return ObjectUtil.isEmpty(obj);
	}

	private static void throwException(String msg) {
		throwException(null, msg);
	}

	private static void throwException(GroupErrorEnum errorEnum, Object... arg) {
		int code = ResponseEnum.SYSTEM_BUSY.getCode();
		String msg = "";
		if (Objects.isNull(errorEnum)) {
			msg = ResponseEnum.SYSTEM_BUSY.getMsg();
		}
		if (arg.length > 0) {
			msg = arg[0].toString();
		}

		throw new BizException(code, msg);
	}

	//如果不是true，则抛异常
	public static void isTrue(boolean expression, String msg) {
		if (!expression) {
			throwException(msg);
		}
	}

	public static void isTrue(boolean expression, GroupErrorEnum errorEnum) {
		if (!expression) {
			throwException(errorEnum, errorEnum == null? "": errorEnum.getMsg());
		}
	}

	public static void isTrue(boolean expression, GroupErrorEnum errorEnum, Object... args) {
		if (!expression) {
			throwException(errorEnum, args);
		}
	}

	//如果是true，则抛异常
	public static void isFalse(boolean expression, String msg) {
		if (expression) {
			throwException(msg);
		}
	}

	//如果是true，则抛异常
	public static void isFalse(boolean expression, GroupErrorEnum errorEnum, Object... args) {
		if (expression) {
			throwException(errorEnum, args);
		}
	}


	public static void equal(Object o1, Object o2, String msg) {
		if (!ObjectUtil.equal(o1, o2)) {
			throwException(msg);
		}
	}

	public static void notEqual(Object o1, Object o2, String msg) {
		if (ObjectUtil.equal(o1, o2)) {
			throwException(msg);
		}
	}

	//如果不是非空对象，则抛异常
	public static void isEmpty(Object obj, String msg) {
		if (!isEmpty(obj)) {
			throwException(msg);
		}
	}

	//如果不是非空对象，则抛异常
	public static void isNotEmpty(Object obj, String msg) {
		if (isEmpty(obj)) {
			throwException(msg);
		}
	}

	//如果不是非空对象，则抛异常
	public static void isNotEmpty(Object obj, GroupErrorEnum errorEnum, Object... args) {
		if (isEmpty(obj)) {
			throwException(errorEnum, args);
		}
	}

	/**
	 * 注解验证参数(全部校验,抛出异常)
	 *
	 * @param obj
	 */
	public static <T> void allCheckValidateThrow(T obj) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
		if (!constraintViolations.isEmpty()) {
			StringBuilder errorMsg = new StringBuilder();
			for (ConstraintViolation<T> violation : constraintViolations) {
				//拼接异常信息
				errorMsg.append(violation.getPropertyPath().toString()).append(":").append(violation.getMessage()).append(",");
			}
			//去掉最后一个逗号
			throwException(GroupErrorEnum.PARAM_VALID, errorMsg.substring(0, errorMsg.length() - 1));
		}
	}

	/**
     * 校验遇到第一个不合法的字段直接返回不合法字段，后续字段不再校验
     *
     * @since 2024年06月24日11:31:14
     * @param <T> 实体泛型
     * @param domain 实体
     * @return
     */
    public static <T> Set<ConstraintViolation<T>> validateFastSneaky(T domain) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR_FAST.validate(domain);
        if (!validateResult.isEmpty()) {
            log.warn("{}:{}", validateResult.iterator().next().getPropertyPath(), validateResult.iterator().next().getMessage());
        }
        return validateResult;
    }

    public static <T> void validateFast(T domain) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR_FAST.validate(domain);
        if (!validateResult.isEmpty()) {
            log.warn("{}:{}", validateResult.iterator().next().getPropertyPath(), validateResult.iterator().next().getMessage());
            throw BizException.wrap("{}:{}", validateResult.iterator().next().getPropertyPath(), validateResult.iterator().next().getMessage());
        }
    }

    /**
     * 校验所有字段并返回不合法字段
     * @since 2024年06月24日11:31:36
     * @param <T>
     * @param domain
     * @return
     * @throws Exception
     */
    public static <T> Set<ConstraintViolation<T>> validateAllSneaky(T domain) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR_ALL.validate(domain);
        if (!validateResult.isEmpty()) {
            Iterator<ConstraintViolation<T>> it = validateResult.iterator();
            while (it.hasNext()) {
                ConstraintViolation<T> cv = it.next();

                Field field = ReflectUtil.getField(cv.getRootBeanClass(), cv.getPropertyPath().toString());
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                String name = "";
                if (excelProperty == null) {
                    Schema apiModelProperty = field.getAnnotation(Schema.class);
                    name = apiModelProperty != null ? apiModelProperty.description() : "";
                } else {
                    name = StrUtil.join(".", excelProperty.value());
                }

                log.warn("{}:{} :{}", name, cv.getPropertyPath(), cv.getMessage());
            }
        }
        return validateResult;
    }


    public static <T> String validateAllSneaky(List<T> domains, int headRow) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < domains.size(); i++) {
            T domain = domains.get(i);
            Set<ConstraintViolation<T>> validateResult = VALIDATOR_ALL.validate(domain);
            if (!validateResult.isEmpty()) {
                Iterator<ConstraintViolation<T>> it = validateResult.iterator();
                while (it.hasNext()) {
                    ConstraintViolation<T> cv = it.next();

                    Field field = ReflectUtil.getField(cv.getRootBeanClass(), cv.getPropertyPath().toString());
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    String name = "";
                    if (excelProperty != null) {
                        name = StrUtil.join(".", excelProperty.value());
                    }
                    if (StrUtil.isEmpty(name)) {
                        Schema apiModelProperty = field.getAnnotation(Schema.class);
                        name = apiModelProperty != null ? apiModelProperty.description() : "";
                    }
                    if (StrUtil.isEmpty(name)) {
                        name = field.getName();
                    }

                    Object value = BeanUtil.getFieldValue(domain, field.getName());
                    sb.append(StrUtil.format("第{}行，{} = {} : {}<br/>", (headRow + i + 1), name, value, cv.getMessage()));

                }
            }
        }

        return sb.toString();
    }


    public static Configuration<HibernateValidatorConfiguration> warp(HibernateValidatorConfiguration configuration) {
        addValidatorMapping(configuration);
        //其他操作
        return configuration;
    }

    private static void addValidatorMapping(HibernateValidatorConfiguration configuration) {
        // 增加一个我们自定义的校验处理器与length的映射
        GetterPropertySelectionStrategy getterPropertySelectionStrategyToUse = new DefaultGetterPropertySelectionStrategy();
        PropertyNodeNameProvider defaultPropertyNodeNameProvider = new DefaultPropertyNodeNameProvider();
        ConstraintMapping mapping = new DefaultConstraintMapping(new JavaBeanHelper(getterPropertySelectionStrategyToUse, defaultPropertyNodeNameProvider));

        ConstraintDefinitionContext<Length> length = mapping.constraintDefinition(Length.class);
        length.includeExistingValidators(true);
        length.validatedBy(LengthConstraintValidator.class);

        ConstraintDefinitionContext<NotNull> notNull = mapping.constraintDefinition(NotNull.class);
        notNull.includeExistingValidators(true);
        notNull.validatedBy(NotNullConstraintValidator.class);

        ConstraintDefinitionContext<NotEmpty> notEmpty = mapping.constraintDefinition(NotEmpty.class);
        notEmpty.includeExistingValidators(true);
        notEmpty.validatedBy(NotEmptyConstraintValidator.class);

        ConstraintDefinitionContext<NotEmptyPattern> notEmptyPattern = mapping.constraintDefinition(NotEmptyPattern.class);
        notEmptyPattern.includeExistingValidators(true);
        notEmptyPattern.validatedBy(NotEmptyPatternConstraintValidator.class);

        configuration.addMapping(mapping);
    }

}
