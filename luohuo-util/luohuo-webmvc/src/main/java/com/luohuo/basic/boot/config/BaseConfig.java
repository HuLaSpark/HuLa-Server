package com.luohuo.basic.boot.config;

import com.luohuo.basic.base.R;
import com.luohuo.basic.converter.String2DateConverter;
import com.luohuo.basic.converter.String2LocalDateConverter;
import com.luohuo.basic.converter.String2LocalDateTimeConverter;
import com.luohuo.basic.converter.String2LocalTimeConverter;
import com.luohuo.basic.jackson.LuohuoJacksonModule;
import com.luohuo.basic.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 基础配置类
 *
 * @author 乾乾
 * @date 2019-06-22 22:53
 */
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public abstract class BaseConfig {

    @Value("${luohuo.version:${info.version:}}")
    private String version;

    @Bean
    public Object configureApiResult() {
        R.setBaseVersion(version);
        return R.success();
    }

    @Bean
    @ConditionalOnMissingBean
    public LuohuoJacksonModule luohuoJacksonModule() {
        return new LuohuoJacksonModule();
    }

    @Bean
    @ConditionalOnClass(JsonMapper.class)
    public JsonMapperBuilderCustomizer luohuoJsonMapperBuilderCustomizer(LuohuoJacksonModule luohuoJacksonModule) {
        return builder -> configureJsonMapperBuilder(builder, luohuoJacksonModule);
    }

    @Bean
    @Primary
    @ConditionalOnClass(JsonMapper.class)
    @ConditionalOnMissingBean
    public JsonMapper jsonMapper(LuohuoJacksonModule luohuoJacksonModule) {
        return configureJsonMapperBuilder(JsonMapper.builderWithJackson2Defaults(), luohuoJacksonModule).build();
    }

    @Bean
    @ConditionalOnClass(JacksonJsonHttpMessageConverter.class)
    public WebMvcConfigurer luohuoJacksonMvcConfigurer(JsonMapper jsonMapper) {
        JsonMapper configuredJsonMapper = jsonMapper.rebuild().build();
        return new WebMvcConfigurer() {
            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                boolean jackson3ConverterConfigured = false;
                for (int i = 0; i < converters.size(); i++) {
                    HttpMessageConverter<?> converter = converters.get(i);
                    if (converter instanceof JacksonJsonHttpMessageConverter jackson3Converter) {
                        JacksonJsonHttpMessageConverter configuredConverter = new JacksonJsonHttpMessageConverter(configuredJsonMapper);
                        configuredConverter.setSupportedMediaTypes(jackson3Converter.getSupportedMediaTypes());
                        converters.set(i, configuredConverter);
                        jackson3ConverterConfigured = true;
                    }
                }
                if (!jackson3ConverterConfigured) {
                    converters.add(0, new JacksonJsonHttpMessageConverter(configuredJsonMapper));
                }
            }
        };
    }

    private JsonMapper.Builder configureJsonMapperBuilder(JsonMapper.Builder builder, LuohuoJacksonModule luohuoJacksonModule) {
        return builder
                .defaultLocale(Locale.CHINA)
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .disable(tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(luohuoJacksonModule);
    }

    @Bean
    public Converter<String, Date> dateConvert() {
        return new String2DateConverter();
    }

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new String2LocalDateConverter();
    }

    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new String2LocalTimeConverter();
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new String2LocalDateTimeConverter();
    }

    @Bean
    public SpringUtils getSpringUtils(ApplicationContext applicationContext) {
        SpringUtils instance = SpringUtils.getInstance();
        SpringUtils.setApplicationContext(applicationContext);
        return instance;
    }
}
