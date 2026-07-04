package com.luohuo.basic.jackson;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.converter.EnumSerializer;
import com.luohuo.basic.converter.LuohuoLocalDateTimeDeserializer;
import com.luohuo.basic.interfaces.BaseEnum;
import com.luohuo.basic.utils.StrPool;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_FORMAT;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_TIME_FORMAT;

/**
 * Jackson 自定义序列化和反序列化规则。
 *
 * @author 乾乾
 */
public class LuohuoJacksonModule extends SimpleModule {

    public LuohuoJacksonModule() {
        super();
        this.setDeserializerModifier(new EnumCodeDeserializerModifier());
        this.addDeserializer(LocalDateTime.class, LuohuoLocalDateTimeDeserializer.INSTANCE);
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        this.addSerializer(LocalDateTime.class, new LocalDateTimeToTimestampSerializer());
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
    }

    private static final class EnumCodeDeserializerModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type,
                                                           BeanDescription.Supplier beanDesc,
                                                           ValueDeserializer<?> deserializer) {
            return new EnumCodeDeserializer(type, deserializer);
        }
    }

    private static final class EnumCodeDeserializer extends ValueDeserializer<Object> {
        private final JavaType enumType;
        private final ValueDeserializer<?> delegate;

        private EnumCodeDeserializer(JavaType enumType, ValueDeserializer<?> delegate) {
            this.enumType = enumType;
            this.delegate = delegate;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            if (parser.hasToken(JsonToken.VALUE_STRING)) {
                String text = parser.getText();
                if (StrUtil.isBlank(text) || StrPool.NULL.equals(text)) {
                    return null;
                }
            }
            if (parser.hasToken(JsonToken.START_OBJECT)) {
                JsonNode node = parser.readValueAsTree();
                JsonNode code = node.get(EnumSerializer.ALL_ENUM_KEY_FIELD);
                String text = code != null ? code.asString() : node.asString();
                if (StrUtil.isBlank(text) || StrPool.NULL.equals(text)) {
                    return null;
                }
                Object value = findEnum(text);
                if (value != null) {
                    return value;
                }
                return context.handleWeirdStringValue(enumType.getRawClass(), text,
                        "not one of the values accepted for Enum class");
            }
            return delegate.deserialize(parser, context);
        }

        @Override
        public Class<?> handledType() {
            return enumType.getRawClass();
        }

        private Object findEnum(String text) {
            Object[] constants = enumType.getRawClass().getEnumConstants();
            if (constants == null) {
                return null;
            }
            for (Object constant : constants) {
                Enum<?> enumValue = (Enum<?>) constant;
                if (text.equals(enumValue.name()) || text.equals(enumValue.toString())) {
                    return enumValue;
                }
                if (constant instanceof BaseEnum baseEnum
                        && (text.equals(baseEnum.getCode()) || text.equals(baseEnum.getValue()))) {
                    return constant;
                }
            }
            return null;
        }
    }
}
