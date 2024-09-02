package com.lucky.around.meal.cache.service;

import org.springframework.data.redis.core.*;
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
}
