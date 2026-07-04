package com.luohuo.basic.converter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ValueDeserializer;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_FORMAT;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN_MATCHES;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_MATCHES;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_EN;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_EN_MATCHES;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_MATCHES;
import static com.luohuo.basic.utils.DateUtils.SLASH_DATE_FORMAT;
import static com.luohuo.basic.utils.DateUtils.SLASH_DATE_FORMAT_MATCHES;
import static com.luohuo.basic.utils.DateUtils.SLASH_DATE_TIME_FORMAT;
import static com.luohuo.basic.utils.DateUtils.SLASH_DATE_TIME_FORMAT_MATCHES;

/**
 * 字段类型是 LocalDateTime 时，支持多种常用格式反序列化。
 *
 * @author 乾乾
 * @date 2020/6/18 上午10:50
 */
@SuppressWarnings("ALL")
public class LuohuoLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {
    public static final LuohuoLocalDateTimeDeserializer INSTANCE = new LuohuoLocalDateTimeDeserializer();
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_EN_DTF = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_EN);
    private static final DateTimeFormatter SLASH_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern(SLASH_DATE_FORMAT);
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_EN_DTF = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT_EN);
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern(SLASH_DATE_TIME_FORMAT);

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            String string = parser.getText().trim();
            if (string.isEmpty()) {
                return null;
            }
            try {
                return parseString(string);
            } catch (DateTimeException e) {
                return context.reportInputMismatch(LocalDateTime.class, "Cannot deserialize LocalDateTime from value '%s'", string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            return parseArray(parser, context);
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return Instant.ofEpochMilli(parser.getLongValue()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            Object embeddedObject = parser.getEmbeddedObject();
            return embeddedObject instanceof LocalDateTime localDateTime ? localDateTime : null;
        }
        return context.reportInputMismatch(LocalDateTime.class, "当前参数需要数组、字符串、时间戳。");
    }

    @Override
    public Class<?> handledType() {
        return LocalDateTime.class;
    }

    private LocalDateTime parseString(String source) {
        LocalDateTime converted = convert(source);
        if (converted != null) {
            return converted;
        }
        if (source.length() > 10 && source.charAt(10) == 'T') {
            if (source.endsWith("Z")) {
                return LocalDateTime.ofInstant(Instant.parse(source), ZoneOffset.UTC);
            }
            return LocalDateTime.parse(source, DEFAULT_FORMATTER);
        }
        return LocalDateTime.parse(source, DEFAULT_FORMATTER);
    }

    private LocalDateTime convert(String source) {
        if (source.matches(DEFAULT_DATE_FORMAT_MATCHES)) {
            return LocalDateTime.of(LocalDate.parse(source, DEFAULT_DATE_FORMAT_DTF), LocalTime.MIN);
        }
        if (source.matches(DEFAULT_DATE_FORMAT_EN_MATCHES)) {
            return LocalDateTime.of(LocalDate.parse(source, DEFAULT_DATE_FORMAT_EN_DTF), LocalTime.MIN);
        }
        if (source.matches(SLASH_DATE_FORMAT_MATCHES)) {
            return LocalDateTime.of(LocalDate.parse(source, SLASH_DATE_FORMAT_DTF), LocalTime.MIN);
        }
        if (source.matches(DEFAULT_DATE_TIME_FORMAT_MATCHES)) {
            return LocalDateTime.parse(source, DEFAULT_DATE_TIME_FORMAT_DTF);
        }
        if (source.matches(DEFAULT_DATE_TIME_FORMAT_EN_MATCHES)) {
            return LocalDateTime.parse(source, DEFAULT_DATE_TIME_FORMAT_EN_DTF);
        }
        if (source.matches(SLASH_DATE_TIME_FORMAT_MATCHES)) {
            return LocalDateTime.parse(source, SLASH_DATE_TIME_FORMAT_DTF);
        }
        return null;
    }

    private LocalDateTime parseArray(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonToken token = parser.nextToken();
        if (token == JsonToken.END_ARRAY) {
            return null;
        }
        if ((token == JsonToken.VALUE_STRING || token == JsonToken.VALUE_EMBEDDED_OBJECT)
                && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            LocalDateTime parsed = deserialize(parser, context);
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                context.reportWrongTokenException(LocalDateTime.class, JsonToken.END_ARRAY, "Expected array to end");
            }
            return parsed;
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            int day = parser.nextIntValue(-1);
            int hour = parser.nextIntValue(-1);
            int minute = parser.nextIntValue(-1);

            token = parser.nextToken();
            if (token == JsonToken.END_ARRAY) {
                return LocalDateTime.of(year, month, day, hour, minute);
            }

            int second = parser.getIntValue();
            token = parser.nextToken();
            if (token == JsonToken.END_ARRAY) {
                return LocalDateTime.of(year, month, day, hour, minute, second);
            }

            int partialSecond = parser.getIntValue();
            if (partialSecond < 1_000) {
                partialSecond *= 1_000_000;
            }
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                context.reportWrongTokenException(LocalDateTime.class, JsonToken.END_ARRAY, "Expected array to end");
            }
            return LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
        }
        return context.reportInputMismatch(LocalDateTime.class,
                "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", token);
    }
}
