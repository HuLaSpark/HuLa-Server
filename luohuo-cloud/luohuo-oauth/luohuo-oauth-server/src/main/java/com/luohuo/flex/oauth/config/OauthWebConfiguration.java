package com.luohuo.flex.oauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.boot.config.BaseConfig;
import com.luohuo.basic.log.event.SysLogListener;
import com.luohuo.basic.utils.BeanPlusUtil;
import com.luohuo.flex.base.service.system.BaseOperationLogService;
import com.luohuo.flex.base.vo.save.system.BaseOperationLogSaveVO;
import com.luohuo.flex.common.properties.SystemProperties;

/**
 * @author 乾乾
 * @date 2017-12-15 14:42
 */
@Configuration
@EnableConfigurationProperties(SystemProperties.class)
public class OauthWebConfiguration extends BaseConfig {

    /**
     * luohuo.log.enabled = true 并且 luohuo.log.type=DB时实例该类
     */
    @Bean
    @ConditionalOnExpression("${luohuo.log.enabled:true} && 'DB'.equals('${luohuo.log.type:LOGGER}')")
    public SysLogListener sysLogListener(BaseOperationLogService logApi) {
        return new SysLogListener(data -> logApi.save(BeanPlusUtil.toBean(data, BaseOperationLogSaveVO.class)));
    }
}
