package com.transit.backend.config.parallel;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
public class EnableSecurityCtxForThreadPool {
	
	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(8);
		executor.setMaxPoolSize(15);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("async-");
		
		return executor;
	}
	
	@Bean
	public DelegatingSecurityContextAsyncTaskExecutor taskExecutor
			(@Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor delegate) {
		return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
	}
}