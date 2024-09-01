package com.lucky.around.meal.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

  private final RedisConnectionFactory redisConnectionFactory;

  public CacheConfig(RedisConnectionFactory redisConnectionFactory) {
    this.redisConnectionFactory = redisConnectionFactory;
  }

  @Bean
  public CacheManager cacheManager() {
    // Redis 캐시 기본 설정 defaultConfiguration
    RedisCacheConfiguration defaultConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            // 캐시 키 직렬화, JSON 형식 직렬화
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()));

    // 특정 캐시 관리 (MAP)
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    // regionList - 시군구 목록 조회 캐싱 TEST 1분
    cacheConfigurations.put("regionList", defaultConfiguration.entryTtl(Duration.ofMinutes(1)));

    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
        .cacheDefaults(defaultConfiguration)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
