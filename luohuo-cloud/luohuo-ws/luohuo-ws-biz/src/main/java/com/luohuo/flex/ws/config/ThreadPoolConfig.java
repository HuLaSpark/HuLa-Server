package com.luohuo.flex.ws.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luohuo.flex.ws.ReactiveContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置
 *
 * @author nyh
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    /**
     * 项目共用线程池
     */
    public static final String LUOHUO_EXECUTOR = "luohuoExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

	@Bean(destroyMethod = "shutdown")
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(4,
				new ThreadFactoryBuilder()
						.setNameFormat("room-timeout-%d")
						.build());
	}

    @Bean(LUOHUO_EXECUTOR)
    @Primary
    public Executor luohuoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("luohuo-executor-");
        // 满了调用线程执行，认为重要任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return ReactiveContextUtil.getContextAwareExecutor(executor);
    }

    @Bean(WS_EXECUTOR)
    public Executor websocketExecutor() {
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
        return ReactiveContextUtil.getContextAwareExecutor(executor);
    }
}