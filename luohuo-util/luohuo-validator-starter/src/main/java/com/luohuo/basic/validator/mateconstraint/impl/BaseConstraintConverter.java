package com.luohuo.basic.validator.mateconstraint.impl;

import cn.hutool.core.map.MapUtil;
import jakarta.validation.constraints.Email;
import com.luohuo.basic.validator.mateconstraint.IConstraintConverter;
import com.luohuo.basic.validator.model.ConstraintInfo;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.lang.RegexPool.EMAIL_WITH_CHINESE;

/**
 * 约束提取基础类
 *
 * @author 乾乾
 * @date 2019-07-14 12:13
 */
public abstract class BaseConstraintConverter implements IConstraintConverter {

    private final List<String> methods = Collections.emptyList();

    /**
     * 支持的类型
     *
     * @param clazz 类型
     * @return 是否支持
     */
    @Override
    public boolean support(Class<? extends Annotation> clazz) {
        if (getSupport().isEmpty()) {
            return true;
        }
        return clazz != null && getSupport().contains(clazz);
    }

    /**
     * 转换
     *
     * @param ano 注解
     * @return 约束信息
     * @throws Exception 异常信息
     */
    @Override
    public ConstraintInfo converter(Annotation ano) throws Exception {
        Class<? extends Annotation> clazz = ano.getClass();
        Map<String, Object> attr = MapUtil.newHashMap();
        for (String method : getMethods()) {
            Object value = clazz.getMethod(method).invoke(ano);
            if (ano instanceof Email && "regexp".equals(method) && Email.class.getMethod("regexp").getDefaultValue().equals(value)) {
                value = EMAIL_WITH_CHINESE;
            }
            attr.put(method, value);
        }
        return new ConstraintInfo().setType(getType(ano.annotationType())).setAttrs(attr);
    }

    /**
     * 子类返回各自具体支持的验证注解 类型
     *
     * @return 注解
     */
    protected abstract List<Class<? extends Annotation>> getSupport();

    /**
     * 子类返回需要反射的验证注解的 字段值
     *
     * @return 方法
     */
    protected List<String> getMethods() {
        return methods;
    }

    /**
     * 子类返回自定义的类型
     *
     * @param type 注解类型
     * @return
     */
    protected abstract String getType(Class<? extends Annotation> type);

}
