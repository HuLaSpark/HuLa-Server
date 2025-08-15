package com.luohuo.flex.common.config;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.flex.common.aspect.LuohuoLogAspect;
import com.luohuo.flex.common.cache.CacheKeyModular;
import com.luohuo.flex.common.properties.IgnoreProperties;
import com.luohuo.flex.common.properties.SystemProperties;

/**
 * @author tangyh
 * @version v1.0
 * @date 2021/9/5 8:04 下午
 * @create [2021/9/5 8:04 下午 ] [tangyh] [初始创建]
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication
@EnableConfigurationProperties({SystemProperties.class, IgnoreProperties.class})
public class CommonAutoConfiguration {
    private final SystemProperties systemProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SystemProperties.PREFIX, name = "recordLuohuo", havingValue = "true", matchIfMissing = true)
    public LuohuoLogAspect getLuoHuoLogAspect() {
        return new LuohuoLogAspect(systemProperties);
    }

    @PostConstruct
    public void init() {
        if (StrUtil.isNotEmpty(systemProperties.getCachePrefix())) {
            CacheKeyModular.PREFIX = systemProperties.getCachePrefix();
            log.info("检查到配置文件中：{}.cachePrefix={}", SystemProperties.PREFIX, systemProperties.getCachePrefix());
        }
    }


}
