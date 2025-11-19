package com.luohuo.basic.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * sql过滤
 *
 * @author rbellia
 * @version 0.1
 */
public final class AntiSqlFilterUtils {
    private static final String[] KEY_WORDS = {";", "\"", "'", "/*", "*/", "--", "exec",
            "select", "update", "delete", "insert", "alter", "drop", "create", "shutdown"};

    private AntiSqlFilterUtils() {
    }

    public static Map<String, String[]> getSafeParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String[]> map = new HashMap<>(CollHelper.initialCapacity(parameterMap.size()));
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] oldValues = entry.getValue();
            map.put(entry.getKey(), getSafeValues(oldValues));
        }
        return map;
    }

    public static String[] getSafeValues(String[] oldValues) {
        if (ArrayUtil.isNotEmpty(oldValues)) {
            String[] newValues = new String[oldValues.length];
            for (int i = 0; i < oldValues.length; i++) {
                newValues[i] = getSafeValue(oldValues[i]);
            }
            return newValues;
        }
        return null;
    }

    public static String getSafeValue(String oldValue) {
        if (StrUtil.isEmpty(oldValue)) {
            return oldValue;
        }
        StringBuilder sb = new StringBuilder(oldValue);
        String lowerCase = oldValue.toLowerCase();
        for (String keyWord : KEY_WORDS) {
            int x;
            while ((x = lowerCase.indexOf(keyWord)) >= 0) {
                if (keyWord.length() == 1) {
                    sb.replace(x, x + 1, " ");
                    lowerCase = sb.toString().toLowerCase();
                    continue;
                }
                sb.delete(x, x + keyWord.length());
                lowerCase = sb.toString().toLowerCase();
            }
        }
        return sb.toString();
    }

}
