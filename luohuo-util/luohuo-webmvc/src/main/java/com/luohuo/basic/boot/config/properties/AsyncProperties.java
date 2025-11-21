package com.luohuo.basic.boot.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.luohuo.basic.constant.Constants;

import static com.luohuo.basic.boot.config.properties.AsyncProperties.PREFIX;

/**
 * 异步线程配置
 *
 * @author 乾乾
 * @date 2021/6/23 8:06 下午
 * @create [2021/6/23 8:06 下午 ] [tangyh] [初始创建]
 */
@Getter
@Setter
@ConfigurationProperties(PREFIX)
public class AsyncProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".async";
    private boolean enabled = true;
    /**
     * 异步核心线程数，默认：2
     */
    private int corePoolSize = 2;
    /**
     * 异步最大线程数，默认：50
     */
    private int maxPoolSize = 50;
    /**
     * 队列容量，默认：10000
     */
    private int queueCapacity = 10000;
    /**
     * 线程存活时间，默认：300
     */
    private int keepAliveSeconds = 300;
    /**
     * 线程名前缀
     */
    private String threadNamePrefix = "luohuo-async-executor-";
}
