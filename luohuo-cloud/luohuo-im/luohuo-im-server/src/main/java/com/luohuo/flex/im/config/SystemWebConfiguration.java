package com.luohuo.flex.im.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.boot.config.BaseConfig;
import com.luohuo.basic.log.event.SysLogListener;
import com.luohuo.flex.oauth.facade.LogFacade;

/**
 * @author 乾乾
 * @date 2017-12-15 14:42
 */
@Configuration
public class SystemWebConfiguration extends BaseConfig {

    /**
     * luohuo.log.enabled = true 并且 luohuo.log.type=DB时实例该类
     */
    @Bean
    @ConditionalOnExpression("${luohuo.log.enabled:true} && 'DB'.equals('${luohuo.log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogFacade logApi) {
        return new SysLogListener(logApi::save);
    }
}
