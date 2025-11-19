package com.luohuo.basic.cloud.feign;

import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_FORMAT;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;
import static com.luohuo.basic.utils.DateUtils.DEFAULT_TIME_FORMAT;

/**
 * 在feign调用方配置， 解决入参和出参是以下类型.
 * 1. @RequestParam("date") Date date
 * 2. @RequestParam("date") LocalDateTime date
 * 3. @RequestParam("date") LocalDate date
 * 4. @RequestParam("date") LocalTime date
 *
 * @author 乾乾
 */
public class DateFormatRegister implements FeignFormatterRegistrar {

    public DateFormatRegister() {
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(Date.class, String.class, new Date2StringConverter());
        registry.addConverter(LocalDateTime.class, String.class, new LocalDateTime2StringConverter());
        registry.addConverter(LocalDate.class, String.class, new LocalDate2StringConverter());
        registry.addConverter(LocalTime.class, String.class, new LocalTime2StringConverter());
    }

    private static class Date2StringConverter implements Converter<Date, String> {
        @Override
        public String convert(Date source) {
            return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(source);
        }
    }

    private static class LocalDateTime2StringConverter implements Converter<LocalDateTime, String> {
        @Override
        public String convert(LocalDateTime source) {
            return source.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
        }
    }

    private static class LocalDate2StringConverter implements Converter<LocalDate, String> {
        @Override
        public String convert(LocalDate source) {
            return source.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
        }
    }

    private static class LocalTime2StringConverter implements Converter<LocalTime, String> {
        @Override
        public String convert(LocalTime source) {
            return source.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
        }
    }
}
