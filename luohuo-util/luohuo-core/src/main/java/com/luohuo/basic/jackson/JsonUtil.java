package com.luohuo.basic.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.code.ResponseEnum;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.basic.utils.StrPool;

import java.io.IOException;
import java.io.InputStream;
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
 * 对 jack json 进行封装
 *
 * @author 乾乾
 */
@Slf4j
public final class JsonUtil {
	private static final ObjectMapper jsonMapper = newInstance();

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

    public static <T> T parse(String content, Class<T> valueType) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        try {
            return getInstance().convertValue(content, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parse(String content, TypeReference<T> typeReference) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
		return getInstance().convertValue(content, typeReference);
	}

    public static <T> T parse(byte[] bytes, Class<T> valueType) {
		return getInstance().convertValue(bytes, valueType);
    }

    public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
		return getInstance().convertValue(bytes, typeReference);
    }

    public static <T> T parse(InputStream in, Class<T> valueType) {
		return getInstance().convertValue(in, valueType);
    }

    public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
		return getInstance().convertValue(in, typeReference);
    }

	/**
	 * 这个方法解析纯json字符串的
	 * @param content json字符串
	 * @param valueTypeRef 对应的类型
	 */
	public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
		if (StrUtil.isEmpty(content)) {
			return Collections.emptyList();
		}
		try {
			if (!StrUtil.startWith(content, StrPool.LEFT_SQ_BRACKET)) {
				content = StrPool.LEFT_SQ_BRACKET + content + StrPool.RIGHT_SQ_BRACKET;
			}
			List<Map<String, Object>> list = getInstance().readValue(content, new TypeReference<>() {});
			return list.stream().map((map) -> toPojo(map, valueTypeRef)).toList();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return Collections.emptyList();
	}

    public static Map<String, Object> toMap(String content) {
		return getInstance().convertValue(content, Map.class);
    }

    public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
		Map<String, Map<String, Object>> map = getInstance().convertValue(content, new TypeReference<>() {
		});
		Map<String, T> result = new HashMap<>(CollHelper.initialCapacity(map.size()));
		map.forEach((key, value) -> result.put(key, toPojo(value, valueTypeRef)));

		return result;
    }

    public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
        return getInstance().convertValue(fromValue, toValueType);
    }

    public static <T> T toPojo(JsonNode resultNode, Class<T> toValueType) {
        return getInstance().convertValue(resultNode, toValueType);
    }

    public static JsonNode readTree(String jsonString) {
        try {
            return getInstance().readTree(jsonString);
        } catch (IOException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonNode readTree(InputStream in) {
        try {
            return getInstance().readTree(in);
        } catch (IOException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonNode readTree(byte[] content) {
        try {
            return getInstance().readTree(content);
        } catch (IOException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static JsonNode readTree(JsonParser jsonParser) {
        try {
            return getInstance().readTree(jsonParser);
        } catch (IOException e) {
            throw new BizException(ResponseEnum.JSON_PARSE_ERROR.getCode(), e);
        }
    }

    public static ObjectMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    public static ObjectMapper newInstance() {
        return new JacksonObjectMapper();
    }

    private static class JacksonHolder {
        private static final ObjectMapper INSTANCE = new JacksonObjectMapper();
    }

    public static class JacksonObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 1L;

        public JacksonObjectMapper() {
            super();
            // 参考BaseConfig
            super.setLocale(Locale.CHINA)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                    .setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.CHINA))
                    .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
                    .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
                    .findAndRegisterModules()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                    .getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            super.registerModule(new LuohuoJacksonModule());
            super.findAndRegisterModules();
        }
    }

	public static JsonNode toJsonNode(String str) {
		try {
			return jsonMapper.readTree(str);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public static <T> T nodeToValue(JsonNode node, Class<T> clz) {
		try {
			return jsonMapper.treeToValue(node, clz);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
