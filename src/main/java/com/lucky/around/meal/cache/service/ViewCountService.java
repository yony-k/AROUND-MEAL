package com.lucky.around.meal.cache.service;

import java.util.Set;

import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewCountService {

  private final StringRedisTemplate redisTemplate;
  private static final String VIEW_COUNT_KEY_PREFIX = "viewCount:";

  private String buildKey(String restaurantId) {
    return VIEW_COUNT_KEY_PREFIX + restaurantId;
  }

  // 조회수 증가
  public void incrementViewCount(String restaurantId) {
    String key = buildKey(restaurantId);
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.increment(key, 1);
  }

  // 특정 맛집의 조회수 가져오기
  public Long getViewCount(String restaurantId) {
    String key = buildKey(restaurantId);
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    String value = ops.get(key);
    return value != null ? Long.parseLong(value) : 0L;
  }

  // 조회수 초기화
  @Scheduled(cron = "0 0 11,17 * * ?") // 매일 오전 11시와 오후 5시에 실행
  public void resetViewCounts() {
    Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");
    if (keys != null) {
      for (String key : keys) {
        redisTemplate.opsForValue().set(key, "0");
      }
    }
  }
}
