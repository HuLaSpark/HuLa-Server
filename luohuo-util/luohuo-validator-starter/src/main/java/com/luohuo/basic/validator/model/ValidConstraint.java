package com.luohuo.basic.validator.model;

import jakarta.validation.groups.Default;

/**
 * 验证约束
 *
 * @author 乾乾
 * @date 2019-07-12 14:32
 */
public class ValidConstraint {

    private final Class<?> target;
    private Class<?>[] groups;

    public ValidConstraint(Class<?> target) {
        this.target = target;
        groups = new Class<?>[]{Default.class};
    }

    public ValidConstraint(Class<?> target, Class<?>[] groups) {
        this.target = target;
        this.groups = groups;
    }

    public Class<?> getTarget() {
        return target;
    }

    public Class<?>[] getGroups() {
        if (groups == null) {
            groups = new Class[0];
        }
        if (groups.length == 0) {
            groups = new Class[]{Default.class};
        }
        return groups;
    }
}
