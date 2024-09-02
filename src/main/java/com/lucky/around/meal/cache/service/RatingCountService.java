package com.lucky.around.meal.cache.service;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.CommonExceptionType;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingCountService {

  private final RestaurantRepository restaurantRepository;
  private final GeometryUtil geometryUtil;
  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${cache.rating-count}")
  private int ratingNumber;

  // 평가 수 기준 객체의 접두사
  private static final String CACHE_KEY_PREFIX = "count:";

  @PostConstruct
  public void init() {
    log.error("평가 수 기준 맛집 목록 업로드");
    cachingByRatingCount();
  }

  // 평가 수 기준 맛집 목록 자동 업로드
  @Scheduled(cron = "0 30 0 * * ?")
  public void cachingByRatingCount() {
    try {
      // 업로드 전 Redis에 올라와있는 목록 삭제
      clearExistingCache();
      // DB에서 필터링하여 맛집 목록 가져오기
      List<Restaurant> findRestaurantByRatingCount =
          restaurantRepository.findRestaurantByRatingCount(ratingNumber);
      // Redis에 저장
      saveToRedis(findRestaurantByRatingCount);
    } catch (Exception e) {
      // info로 바꾸기
      log.error("평가 수 기준 맛집 목록 업로드 실패: ", e);
    }
  }

  private void saveToRedis(List<Restaurant> restaurantList) {
    for (Restaurant restaurant : restaurantList) {
      // 평가 수 기준 전용 키
      String key = CACHE_KEY_PREFIX + restaurant.getId();

      // 필드 값을 해시에 저장
      Map<String, Object> hash = new HashMap<>();
      hash.put("id", restaurant.getId());
      hash.put("restaurantName", restaurant.getRestaurantName());
      hash.put("dosi", restaurant.getDosi());
      hash.put("sigungu", restaurant.getSigungu());
      hash.put("jibunDetailAddress", restaurant.getJibunDetailAddress());
      hash.put("doroDetailAddress", restaurant.getDoroDetailAddress());
      hash.put("category", restaurant.getCategory().name());
      hash.put("restaurantTel", restaurant.getRestaurantTel());
      hash.put("lon", String.valueOf(restaurant.getLocation().getX()));
      hash.put("lat", String.valueOf(restaurant.getLocation().getY()));
      hash.put("ratingAverage", String.valueOf(restaurant.getRatingAverage()));
      // 레디스에 해시 저장
      redisTemplate.opsForHash().putAll(key, hash);
    }
  }

  // 평가 수 기준 맛집 목록 조회
  public List<Restaurant> getRaitinCountList() {
    // 레디스에서 프리픽스에 해당하는 모든 키를 검색
    Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
    if (keys == null || keys.isEmpty()) {
      throw new CustomException(CommonExceptionType.INSPECTION_TIME);
    }

    // Map 형태로 받아와서 HashMap으로 변환 다시 Restaurant로 변환
    List<Restaurant> list =
        keys.stream()
            .map(key -> redisTemplate.opsForHash().entries(key))
            .map(HashMap::new)
            .map(this::toRestaurant) // 변환
            .collect(Collectors.toList());
    return list;
  }

  // 평가 생성 시 레디스 수정
  public void updateRating(String restaurantId, String field, double ratingAverage) {
    String key = CACHE_KEY_PREFIX + restaurantId;
    redisTemplate.opsForHash().put(key, field, String.valueOf(ratingAverage));
    log.info("평가 업데이트 완료");
  }

  // 레디스의 해시맵을 Restaurant로 변환
  private Restaurant toRestaurant(Map<Object, Object> restaurantMap) {
    return Restaurant.builder()
        .id(String.valueOf(restaurantMap.get("id")))
        .restaurantName(String.valueOf(restaurantMap.get("restaurantName")))
        .dosi(String.valueOf(restaurantMap.get("dosi")))
        .sigungu(String.valueOf(restaurantMap.get("sigungu")))
        .jibunDetailAddress(String.valueOf(restaurantMap.get("jibunDetailAddress")))
        .doroDetailAddress(String.valueOf(restaurantMap.get("doroDetailAddress")))
        .category(Category.valueOf(String.valueOf(restaurantMap.get("category"))))
        .restaurantTel(String.valueOf(restaurantMap.get("restaurantTel")))
        .location(
            geometryUtil.createPoint(
                Double.parseDouble(String.valueOf(restaurantMap.get("lon"))),
                Double.parseDouble(String.valueOf(restaurantMap.get("lat")))))
        .ratingAverage(Double.parseDouble(String.valueOf(restaurantMap.get("ratingAverage"))))
        .build();
  }

  // 평가 수 기준 목록 전체 삭제
  private void clearExistingCache() {
    // 평가 수 기준 전용 키로 기존 목록의 키값 가져옴
    Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
    // 키가 존재하면 해당 키들을 삭제
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }
}
