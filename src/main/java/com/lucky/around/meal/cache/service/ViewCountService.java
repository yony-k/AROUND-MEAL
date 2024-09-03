package com.lucky.around.meal.cache.service;

import java.util.Set;

import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

  private final StringRedisTemplate redisTemplate;
  private final RestaurantRepository restaurantRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String VIEW_COUNT_KEY_PREFIX = "viewCount:";
  private static final String HOURLY_VIEW_COUNT_KEY_PREFIX = "hourlyViewCount:";
  private static final String RESTAURANT_DETAIL_KEY_PREFIX = "restaurantDetail::";
  private static final long VIEW_COUNT_THRESHOLD = 10L;

  //
  //    @PostConstruct
  //    public void init() {
  //        log.error("조회수 기준 캐싱 작업 에러");
  //        updateHourlyViewCountCache();
  //    }

  private String buildKey(String prefix, String restaurantId) {
    return prefix + restaurantId;
  }

  // 조회수 증가
  public void incrementViewCount(String restaurantId) {
    // 전체 조회수 증가
    String key = buildKey(VIEW_COUNT_KEY_PREFIX, restaurantId);
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.increment(key, 1);

    // 1시간 기준 조회수 증가
    String hourlyKey = buildKey(HOURLY_VIEW_COUNT_KEY_PREFIX, restaurantId);
    ops.increment(hourlyKey, 1);
  }

  // 특정 맛집의 전체 조회수 가져오기
  public Long getViewCount(String restaurantId) {
    String key = buildKey(VIEW_COUNT_KEY_PREFIX, restaurantId);
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    String value = ops.get(key);
    return value != null ? Long.parseLong(value) : 0L;
  }

  // 특정 맛집의 1시간 기준 조회수 조회
  public Long getHourlyViewCount(String restaurantId) {
    String key = buildKey(HOURLY_VIEW_COUNT_KEY_PREFIX, restaurantId);
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
