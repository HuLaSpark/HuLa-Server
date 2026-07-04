package com.luohuo.basic.jackson;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.basic.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * JSON 工具封装。
 *
 * @author 乾乾
 */
@Slf4j
public final class JsonUtil {
    private static final JsonMapper JSON_MAPPER = newInstance();

    private JsonUtil() {
    }

    public static <T> String toJson(T value) {
        try {
            return getInstance().writeValueAsString(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return StrPool.EMPTY;
    }

    public static byte[] toJsonAsBytes(Object object) {
        try {
            return getInstance().writeValueAsBytes(object);
        } catch (JacksonException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static <T> T parse(String content, Class<T> valueType) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        String s = content.trim();
        boolean jsonLike = StrUtil.startWith(s, "{") || StrUtil.startWith(s, "[");
        try {
            if (jsonLike) {
                return getInstance().readValue(s, valueType);
            }
            return getInstance().readValue(toJson(content), valueType);
        } catch (Exception e) {
            try {
                return getInstance().readValue(s, valueType);
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    public static <T> T parse(String content, TypeReference<T> typeReference) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        String s = content.trim();
        boolean jsonLike = StrUtil.startWith(s, "{") || StrUtil.startWith(s, "[");
        try {
            if (jsonLike) {
                return getInstance().readValue(s, typeReference);
            }
            return getInstance().readValue(toJson(content), typeReference);
        } catch (Exception e) {
            try {
                return getInstance().readValue(s, typeReference);
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    public static <T> T parseJson(String content, Class<T> valueType) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        try {
            return getInstance().readValue(content, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parseJson(String content, TypeReference<T> typeReference) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        try {
            return getInstance().readValue(content, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parse(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            return getInstance().readValue(text, getInstance().constructType(type));
        } catch (Exception e) {
            log.error("json parse err,json:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(byte[] bytes, Class<T> valueType) {
        try {
            return getInstance().readValue(bytes, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(bytes, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parse(InputStream in, Class<T> valueType) {
        try {
            return getInstance().readValue(in, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(in, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 这个方法解析纯 json 字符串。
     */
    public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
        if (StrUtil.isEmpty(content)) {
            return Collections.emptyList();
        }
        try {
            if (!StrUtil.startWith(content, StrPool.LEFT_SQ_BRACKET)) {
                content = StrPool.LEFT_SQ_BRACKET + content + StrPool.RIGHT_SQ_BRACKET;
            }
            List<Map<String, Object>> list = getInstance().readValue(content, new TypeReference<>() {
            });
            return list.stream().map((map) -> toPojo(map, valueTypeRef)).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public static Map<String, Object> toMap(String content) {
        try {
            return getInstance().readValue(content, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
        try {
            Map<String, Map<String, Object>> map = getInstance().readValue(content, new TypeReference<>() {
            });
            Map<String, T> result = new HashMap<>(CollHelper.initialCapacity(map.size()));
            map.forEach((key, value) -> result.put(key, toPojo(value, valueTypeRef)));
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    public static <T> T toPojo(Object fromValue, Class<T> toValueType) {
        return parse(toJson(fromValue), toValueType);
    }

    public static <T> T toPojo(JsonNode resultNode, Class<T> toValueType) {
        return parse(toJson(resultNode), toValueType);
    }

    public static JsonNode readTree(String jsonString) {
        try {
            return getInstance().readTree(jsonString);
        } catch (JacksonException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonNode readTree(InputStream in) {
        try {
            return getInstance().readTree(in);
        } catch (JacksonException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonNode readTree(byte[] content) {
        try {
            return getInstance().readTree(content);
        } catch (JacksonException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonNode readTree(JsonParser jsonParser) {
        try {
            return getInstance().readTree(jsonParser);
        } catch (JacksonException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    public static JsonMapper newInstance() {
        return JsonMapper.builderWithJackson2Defaults()
                .defaultLocale(Locale.CHINA)
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .defaultDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.CHINA))
                .disable(tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new LuohuoJacksonModule())
                .build();
    }

    private static class JacksonHolder {
        private static final JsonMapper INSTANCE = newInstance();
    }

    public static JsonNode toJsonNode(String str) {
        try {
            return JSON_MAPPER.readTree(str);
        } catch (JacksonException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static <T> T nodeToValue(JsonNode node, Class<T> clz) {
        try {
            return JSON_MAPPER.readValue(toJson(node), clz);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
