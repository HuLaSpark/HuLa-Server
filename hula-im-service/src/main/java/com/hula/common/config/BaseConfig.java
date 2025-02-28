package com.hula.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hula.jackson.JacksonModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author 乾乾
 */
@Configuration
public class BaseConfig {

	/**
	 * 全局配置 序列化和反序列化规则
	 * addSerializer：序列化 （Controller 返回 给前端的json）
	 * 1. Long -> string
	 * 2. BigInteger -> string
	 * 3. BigDecimal -> string
	 * 4. date -> string
	 * 5. LocalDateTime -> "yyyy-MM-dd HH:mm:ss"
	 * 6. LocalDate -> "yyyy-MM-dd"
	 * 7. LocalTime -> "HH:mm:ss"
	 *
	 * <p>
	 * addDeserializer: 反序列化 （前端调用接口时，传递到后台的json）
	 * 2. "yyyy-MM-dd HH:mm:ss" -> LocalDateTime
	 * 3. "yyyy-MM-dd" -> LocalDate
	 * 4. "HH:mm:ss" -> LocalTime
	 *
	 * @return 全局 ObjectMapper
	 */
	@Bean
	@Primary
	@ConditionalOnMissingBean
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		objectMapper
				.setLocale(Locale.CHINA)
				// 去掉默认的时间戳格式
//				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				// 时区
				.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
				// Date参数日期格式为字符串
//				.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN, Locale.CHINA))

				// 该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
				.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
				// 忽略不能转义的字符
				.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
				// 在使用spring boot + jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				// 忽略未知字段
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				// 单引号处理
				.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 注册自定义模块
		objectMapper.registerModule(new JacksonModule()).findAndRegisterModules();

		return objectMapper;
	}
}
