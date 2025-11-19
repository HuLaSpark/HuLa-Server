package com.luohuo.flex.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.boot.config.BaseConfig;
import com.luohuo.basic.constant.Constants;
import com.luohuo.basic.log.event.SysLogListener;
import com.luohuo.flex.oauth.facade.LogFacade;

/**
 * 基础服务-Web配置
 *
 * @author 乾乾
 * @date 2021-10-08
 */
@Configuration
public class BaseWebConfiguration extends BaseConfig {

    /**
     * luohuo.log.enabled = true 并且 luohuo.log.type=DB时实例该类
     */
    @Bean
    @ConditionalOnExpression("${" + Constants.PROJECT_PREFIX + ".log.enabled:true} && 'DB'.equals('${" + Constants.PROJECT_PREFIX + ".log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogFacade logApi) {
        return new SysLogListener(logApi::save);
    }
}
