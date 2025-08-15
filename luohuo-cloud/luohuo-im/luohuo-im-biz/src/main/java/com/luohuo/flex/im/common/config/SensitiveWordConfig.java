package com.luohuo.flex.im.common.config;

import com.luohuo.flex.im.common.utils.sensitiveword.DFAFilter;
import com.luohuo.flex.im.common.utils.sensitiveword.SensitiveWordBs;
import com.luohuo.flex.im.sensitive.MyWordFactory;
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