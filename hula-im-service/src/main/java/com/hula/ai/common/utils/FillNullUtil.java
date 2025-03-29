package com.hula.ai.common.utils;

import com.hula.ai.common.constant.StringPoolConstant;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 填充null值数据
 *
 * @Author 葛盼
 * @date 2020/3/26 16:13
 * 得其道
 * 乾乾
 */
public class FillNullUtil {

    /**
     * 要填充的实体类
     *
     * @param bean
     */
    public static void fillNull(Object bean) {
        if (bean instanceof Map) {
            return;
        }
        if (bean instanceof List) {
            return;
        }
        List<Field> fieldList = new ArrayList<>();
        Class tempClass = bean.getClass();
        fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
        for (Field field : fieldList) {
            field.setAccessible(true);
            if ("java.lang.String".equals(field.getType().getName())) {
                setValue(StringPoolConstant.EMPTY, bean, field);
            } else if ("java.lang.Integer".equals(field.getType().getName())) {
                setValue(Integer.valueOf(0), bean, field);
            } else if ("java.lang.Double".equals(field.getType().getName())) {
                setValue(Double.valueOf(0), bean, field);
            } else if ("java.lang.Long".equals(field.getType().getName())) {
                setValue(Long.valueOf(0), bean, field);
            } else if ("java.lang.Boolean".equals(field.getType().getName())) {
                setValue(true, bean, field);
            } else if ("java.math.BigDecimal".equals(field.getType().getName())) {
                setValue(BigDecimal.ZERO, bean, field);
            } else if ("java.time.LocalDateTime".equals(field.getType().getName())) {
                setValue(LocalDateTime.now(), bean, field);
            } else if ("java.util.Date".equals(field.getType().getName())) {
                setValue(new Date(), bean, field);
            }
        }
    }

    private static void setValue(Object value, Object bean, Field field) {
        if ("id".equals(field.getName())) {
            return;
        }
        try {
            Object mid = field.get(bean);
            if (mid == null) {
                field.set(bean, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String nullToEmpty(Object obj) {
        if (obj == null) {
            return StringPoolConstant.EMPTY;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj + StringPoolConstant.EMPTY;
    }

}
