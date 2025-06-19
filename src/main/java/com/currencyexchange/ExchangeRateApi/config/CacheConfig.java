package com.currencyexchange.ExchangeRateApi.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.cache.caffeine.CaffeineCacheManager;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    @Profile("cache-redis")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
            .build();
    }

    @Bean
    @Profile("cache-caffeine")
    public CacheManager caffeineCacheManager() {
        return new CaffeineCacheManager();
    }
}

