package com.luohuo.flex.im.common.config;

import com.luohuo.basic.context.ContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.luohuo.flex.im.common.factory.MyThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置, 不再实现AsyncConfigurer，底层DefaultAsyncTaskConfig已经做了实现
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

	@Bean(LUOHUO_EXECUTOR)
	@Primary
	public Executor luohuoExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(50);
		executor.setKeepAliveSeconds(60); // 空闲线程存活时间
		executor.setThreadNamePrefix("luohuo-executor-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(30); // 停机等待超时
		executor.setThreadFactory(new MyThreadFactory(executor));
		executor.initialize();
		return ContextUtil.getContextAwareExecutor(executor);
	}
}