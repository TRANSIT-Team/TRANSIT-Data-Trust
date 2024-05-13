package com.transit.backend.config;


import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedissonSpringDataConfig {
	@Bean
	@ConfigurationProperties(prefix = "redisson-config")
	public Config redissonConfig() {
		return new Config();
	}
	
}