package com.hula.common.config;

import com.hula.common.utils.sensitiveword.DFAFilter;
import com.hula.common.utils.sensitiveword.SensitiveWordBs;
import com.hula.sensitive.MyWordFactory;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nyh
 */
@Configuration
public class SensitiveWordConfig {

    @Resource
    private MyWordFactory myWordFactory;

    /**
     * 初始化引导类
     *
     * @return 初始化引导类
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .filterStrategy(DFAFilter.getInstance())
                .sensitiveWord(myWordFactory)
                .init();
    }

}