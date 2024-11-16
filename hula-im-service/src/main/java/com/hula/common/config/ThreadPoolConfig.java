package com.hula.common.config;

import com.hula.common.factory.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置
 *
 * @author nyh
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {
    /**
     * 项目共用线程池
     */
    public static final String HULA_EXECUTOR = "hulaExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

    /**
     * ai聊天机器人线程池
     **/
    public static final String AICHAT_EXECUTOR = "aichatExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return hulaExecutor();
    }

    @Bean(HULA_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor hulaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("hula-executor-");
        //满了调用线程执行，认为重要任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    @Bean(WS_EXECUTOR)
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        // 支持同时推送1000人
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("websocket-executor-");
        // 满了直接丢弃，默认为不重要消息推送
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    @Bean(AICHAT_EXECUTOR)
    public ThreadPoolTaskExecutor chatAiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(15);
        executor.setThreadNamePrefix("aichat-executor-");
        // 满了直接丢弃，默认为不重要消息推送
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        return executor;
    }
}