package com.luohuo.basic.echo.manager;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import com.luohuo.basic.annotation.echo.Echo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类管理器
 *
 * @author 乾乾
 * @date 2021年03月22日15:53:22
 */
@Slf4j
public class ClassManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, List<Field>> CACHE = new HashMap<>();

    public static List<Field> getFields(Class<?> clazz) {
        if (CACHE.containsKey(clazz.getName())) {
            return CACHE.get(clazz.getName());
        }

        Field[] declaredFields = ReflectUtil.getFields(clazz);
        int mod;
        // 循环遍历所有的属性进行判断
        List<Field> fieldList = new ArrayList<>();
        for (Field field : declaredFields) {
            mod = field.getModifiers();
            // 如果是 static, final, volatile, transient 的字段，则直接跳过
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isVolatile(mod)) {
                continue;
            }
            Echo echo = field.getDeclaredAnnotation(Echo.class);
            if (echo == null) {
                continue;
            }
            if (StrUtil.hasEmpty(echo.api())) {
                log.warn("类 {} 属性 [{}] api 为空。", clazz.getName(), field.getName());
                continue;
            }
            fieldList.add(field);
        }
        log.debug("已扫描到{}中存在{}个@Echo字段", clazz.getName(), fieldList.size());
        CACHE.put(clazz.getName(), fieldList);
        return fieldList;
    }
}
