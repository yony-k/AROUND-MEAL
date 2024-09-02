package com.lucky.around.meal.common.config;

import java.time.Duration;
import java.util.*;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

  private final RedisConnectionFactory redisConnectionFactory;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper;
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
    // regionList - 시군구 목록 조회 캐싱 TEST 10분
    cacheConfigurations.put("regionList", defaultConfiguration.entryTtl(Duration.ofMinutes(10)));

    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
        .cacheDefaults(defaultConfiguration)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
