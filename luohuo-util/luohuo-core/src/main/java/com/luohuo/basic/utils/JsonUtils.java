package com.luohuo.basic.utils;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.jackson.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author nyh
 */
@Slf4j
public class JsonUtils {
    public static <T> T toObj(String str, Class<T> clz) {
        return JsonUtil.parseJson(str, clz);
    }

    public static <T> T toObj(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return JsonUtil.parse(text, type);
    }

    public static <T> T toObj(String str, TypeReference<T> clz) {
        return JsonUtil.parseJson(str, clz);
    }

    public static <T> List<T> toList(String str, Class<T> clz) {
        return JsonUtil.parseArray(str, clz);
    }

    public static JsonNode toJsonNode(String str) {
        return JsonUtil.toJsonNode(str);
    }

    public static <T> T nodeToValue(JsonNode node, Class<T> clz) {
        return JsonUtil.nodeToValue(node, clz);
    }

    public static String toStr(Object t) {
        return JsonUtil.toJson(t);
    }
}
