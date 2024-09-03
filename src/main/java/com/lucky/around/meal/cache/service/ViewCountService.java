package com.lucky.around.meal.cache.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.controller.response.RestaurantDetailResponseDto;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.RestaurantExceptionType;
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

  @PostConstruct
  public void init() {
    log.error("조회수 기준 캐싱 작업 에러");
    updateHourlyViewCountCache();
  }

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

  // 1시간 기준 조회수 N개 이상 캐싱처리
  @Scheduled(cron = "0 0 * * * ?") // 매시 정각에 실행
  public void updateHourlyViewCountCache() {
    Set<String> hourlyKeys = redisTemplate.keys(HOURLY_VIEW_COUNT_KEY_PREFIX + "*");
    if (hourlyKeys != null) {
      Map<String, Long> hourlyViewCounts = getHourlyViewCounts(hourlyKeys);
      List<String> highViewRestaurantIds = filterHighViewRestaurants(hourlyViewCounts);

      if (!highViewRestaurantIds.isEmpty()) {
        cacheHighViewRestaurants(highViewRestaurantIds);
      }
    }
  }

  // 조회수 맵 생성
  private Map<String, Long> getHourlyViewCounts(Set<String> keys) {
    return keys.stream()
        .collect(
            Collectors.toMap(
                key -> key.replace(HOURLY_VIEW_COUNT_KEY_PREFIX, ""),
                key -> Long.parseLong(redisTemplate.opsForValue().get(key))));
  }

  // 조회수 기준에 맞는 맛집 필터
  private List<String> filterHighViewRestaurants(Map<String, Long> viewCounts) {
    return viewCounts.entrySet().stream()
        .filter(entry -> entry.getValue() >= VIEW_COUNT_THRESHOLD)
        .map(Map.Entry::getKey)
        .toList();
  }

  // 조회수가 기준에 충족하는 맛집
  private void cacheHighViewRestaurants(List<String> restaurantIds) {
    for (String restaurantId : restaurantIds) {
      Restaurant restaurant = getRestaurantById(restaurantId);
      RestaurantDetailResponseDto dto = mapRestaurantToDto(restaurant);
      cacheRestaurantDetail(restaurantId, dto);
    }
  }

  private Restaurant getRestaurantById(String restaurantId) {
    return restaurantRepository
        .findById(restaurantId)
        .orElseThrow(() -> new CustomException(RestaurantExceptionType.RESTAURANT_NOT_FOUND));
  }

  private void cacheRestaurantDetail(String restaurantId, RestaurantDetailResponseDto dto) {
    try {
      String json = objectMapper.writeValueAsString(dto);
      redisTemplate.opsForValue().set(buildKey(RESTAURANT_DETAIL_KEY_PREFIX, restaurantId), json);
    } catch (JsonProcessingException e) {
      log.error("Json 직렬화 문제", e);
    }
  }

  // 1시간 기준 조회수 초기화
  @Scheduled(cron = "0 0 * * * ?")
  public void resetViewCounts() {
    Set<String> keys = redisTemplate.keys(HOURLY_VIEW_COUNT_KEY_PREFIX + "*");
    if (keys != null) {
      for (String key : keys) {
        redisTemplate.opsForValue().set(key, "0");
      }
    }
  }

  private RestaurantDetailResponseDto mapRestaurantToDto(Restaurant restaurant) {
    return new RestaurantDetailResponseDto(
        restaurant.getId(),
        restaurant.getRestaurantName(),
        restaurant.getDosi(),
        restaurant.getSigungu(),
        restaurant.getJibunDetailAddress(),
        restaurant.getDoroDetailAddress(),
        restaurant.getCategory().name(),
        restaurant.getRestaurantTel(),
        restaurant.getLocation().getX(),
        restaurant.getLocation().getY(),
        restaurant.getRatingAverage(),
        List.of());
  }
}
