package com.luohuo.basic.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDateTime 序列化为毫秒时间戳。
 */
public class LocalDateTimeToTimestampSerializer extends StdSerializer<LocalDateTime> {
    public LocalDateTimeToTimestampSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializationContext context)
            throws JacksonException {
        if (value == null) {
            generator.writeNull();
            return;
        }
        long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        generator.writeNumber(timestamp);
    }
}
