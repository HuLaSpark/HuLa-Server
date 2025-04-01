package com.hula.ai.framework.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.hula.ai.common.constant.StringPoolConstant;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 此modifier主要做的事情为：
 * 1.当序列化类型为数组集合时，当值为null时，序列化成[]
 * 2.String类型值序列化为""
 *
 * @author: 云裂痕
 * @date: 2020/12/29
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class MyBeanSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        // 循环所有的beanPropertyWriter
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            // 判断字段的类型，如果是数组或集合则注册nullSerializer
            if (isArrayType(writer)) {
                // 给writer注册一个自己的nullSerializer
                writer.assignNullSerializer(new NullArrayJsonSerializer());
            }
            if (isStringType(writer)) {
                writer.assignNullSerializer(new NullStringJsonSerializer());
            }
            if (isNumberType(writer)) {
                if (BigDecimal.class.isAssignableFrom(writer.getType().getRawClass())) {
                    writer.assignSerializer(new BigDecimalJsonSerializer());
                }
                writer.assignNullSerializer(new NullNumberJsonSerializer());
            }
            if (isBooleanType(writer)) {
                writer.assignNullSerializer(new NullBooleanJsonSerializer());
            }
            if (isLocalDateTimeType(writer)) {
                writer.assignNullSerializer(new NullLocalDateTimeJsonSerializer());
            }
            if (isObejctType(writer)) {
                writer.assignNullSerializer(new NullObjectJsonSerializer());
            }
        }
        return beanProperties;
    }

    /**
     * 处理数组集合类型的null值
     */
    public static class NullArrayJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeEndArray();
        }
    }

    /**
     * 处理字符串类型的null值
     */
    public static class NullStringJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(StringPoolConstant.EMPTY);
        }
    }

    /**
     * 处理数值类型的null值
     */
    public static class BigDecimalJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(value.toString());
        }
    }

    /**
     * 处理数值类型的null值
     */
    public static class NullNumberJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(0);
        }
    }

    /**
     * 处理boolean类型的null值
     */
    public static class NullBooleanJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeBoolean(false);
        }
    }

    /**
     * 处理LocalDateTime类型的null值
     */
    public static class NullLocalDateTimeJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(StringPoolConstant.EMPTY);
        }
    }

    /**
     * 处理实体对象类型的null值
     */
    public static class NullObjectJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNull();
        }
    }

    /**
     * 是否是数组
     */
    private boolean isArrayType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是String
     */
    private boolean isStringType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是数值类型
     */
    private boolean isNumberType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return Number.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是boolean
     */
    private boolean isBooleanType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.equals(Boolean.class);
    }

    /**
     * 是否是LocalDateTime
     */
    private boolean isLocalDateTimeType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.equals(LocalDateTime.class) || clazz.equals(LocalDate.class) || clazz.equals(LocalTime.class) || clazz.equals(Date.class);
    }

    /**
     * 是否是Object
     */
    private boolean isObejctType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.equals(Object.class);
    }

}

