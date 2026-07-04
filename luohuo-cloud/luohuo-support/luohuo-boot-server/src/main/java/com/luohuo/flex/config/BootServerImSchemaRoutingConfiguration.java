package com.luohuo.flex.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.luohuo.basic.utils.mybatis.MyBatisUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * BootServer is a monolith, but the local dev dump keeps IM tables in a separate schema.
 * This configuration is packaged only in luohuo-boot-server, so cloud services are not affected.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "luohuo.boot.im-schema-routing", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BootServerImSchemaRoutingConfiguration {

    private static final Pattern SCHEMA_NAME = Pattern.compile("[A-Za-z0-9_]+|");
    private static final String DEFAULT_IM_SCHEMA = "luohuo_im_01";

    @Bean
    public static BeanPostProcessor bootServerImSchemaRoutingPostProcessor(Environment environment) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof MybatisPlusInterceptor interceptor) {
                    String schema = environment.getProperty("luohuo.boot.im-schema-routing.schema", DEFAULT_IM_SCHEMA);
                    MyBatisUtils.addInterceptor(interceptor, imSchemaRoutingInterceptor(schema), 0);
                }
                return bean;
            }
        };
    }

    private static InnerInterceptor imSchemaRoutingInterceptor(String schema) {
        DynamicTableNameInnerInterceptor interceptor = new DynamicTableNameInnerInterceptor();
        interceptor.setTableNameHandler((sql, tableName) -> routeTable(schema, tableName));
        return interceptor;
    }

    private static String routeTable(String schema, String tableName) {
        if (!StringUtils.hasText(schema) || !SCHEMA_NAME.matcher(schema).matches()) {
            return tableName;
        }
        String normalizedTable = unwrap(tableName).toLowerCase(Locale.ROOT);
        if (!normalizedTable.startsWith("im_")) {
            return tableName;
        }
        return quote(schema) + "." + quote(unwrap(tableName));
    }

    private static String unwrap(String value) {
        if (value != null && value.length() > 1 && value.startsWith("`") && value.endsWith("`")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static String quote(String value) {
        return "`" + value + "`";
    }
}