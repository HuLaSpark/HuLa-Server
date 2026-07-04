package com.luohuo.flex.gateway.config;

import com.luohuo.basic.converter.String2DateConverter;
import com.luohuo.basic.converter.String2LocalDateConverter;
import com.luohuo.basic.converter.String2LocalDateTimeConverter;
import com.luohuo.basic.converter.String2LocalTimeConverter;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.basic.jackson.LuohuoJacksonModule;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.gateway.service.GarbageEchoServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import tools.jackson.databind.json.JsonMapper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.luohuo.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * @author 乾乾
 * @date 2021/12/9 18:52
 */
@Configuration
public class GatewayWebConfig {
    /**
     * 这个类仅仅是为了防止在gateway启动报错
     */
    @Bean
    @Primary
    public EchoService getGarbageEchoServiceImpl() {
        return new GarbageEchoServiceImpl();
    }

    /**
     * Spring 工具类
     *
     * @param applicationContext 上下文
     */
    @Bean
    public SpringUtils getSpringUtils(ApplicationContext applicationContext) {
        SpringUtils instance = SpringUtils.getInstance();
        SpringUtils.setApplicationContext(applicationContext);
        return instance;
    }

    @Bean
    @Primary
    @ConditionalOnClass(JsonMapper.class)
    @ConditionalOnMissingBean
    public JsonMapper jsonMapper(LuohuoJacksonModule luohuoJacksonModule) {
        return configureJsonMapperBuilder(JsonMapper.builderWithJackson2Defaults(), luohuoJacksonModule).build();
    }

    @Bean
    @ConditionalOnClass(JsonMapper.class)
    public JsonMapperBuilderCustomizer luohuoGatewayJsonMapperBuilderCustomizer(LuohuoJacksonModule luohuoJacksonModule) {
        return builder -> configureJsonMapperBuilder(builder, luohuoJacksonModule);
    }

    @Bean
    @ConditionalOnMissingBean
    public LuohuoJacksonModule luohuoJacksonModule() {
        return new LuohuoJacksonModule();
    }

    private JsonMapper.Builder configureJsonMapperBuilder(JsonMapper.Builder builder, LuohuoJacksonModule luohuoJacksonModule) {
        return builder
                .defaultLocale(Locale.CHINA)
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .defaultDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.CHINA))
                .disable(tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(luohuoJacksonModule);
    }

    /**
     * 解决 @RequestParam(value = "date") Date date
     * date 类型参数 格式问题
     */
    @Bean
    public Converter<String, Date> dateConvert() {
        return new String2DateConverter();
    }

    /**
     * 解决 @RequestParam(value = "time") LocalDate time
     */
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new String2LocalDateConverter();
    }

    /**
     * 解决 @RequestParam(value = "time") LocalTime time
     */
    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new String2LocalTimeConverter();
    }

    /**
     * 解决 @RequestParam(value = "time") LocalDateTime time
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new String2LocalDateTimeConverter();
    }
}