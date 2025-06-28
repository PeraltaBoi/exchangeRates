package com.currencyexchange.ExchangeRateApi.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  @Profile("cache-redis")
  public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration cacheConfig = RedisCacheConfiguration
        .defaultCacheConfig()
        .serializeKeysWith(
            SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()))
        .disableCachingNullValues();

    return RedisCacheManager
        .builder(redisConnectionFactory)
        .cacheDefaults(cacheConfig) // we need this custom config for de/serialization
        .build();
  }

  @Bean
  @Profile("cache-caffeine")
  public CacheManager caffeineCacheManager() {
    return new CaffeineCacheManager();
  }
}
