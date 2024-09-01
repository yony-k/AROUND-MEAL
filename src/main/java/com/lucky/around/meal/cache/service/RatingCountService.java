package com.lucky.around.meal.cache.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.cache.entity.RestaurantForRedis;
import com.lucky.around.meal.cache.repository.RestaurantForRedisRepository;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingCountService {

  private final RestaurantRepository restaurantRepository;
  private final RestaurantForRedisRepository forRedisRepository;

  @Scheduled(cron = "0 30 0 * * ?")
  public void cachingByRatingCount() {
    try {
      // DB에서 필터링하여 맛집 목록 가져오기
      List<Restaurant> findRestaurantByRatingCount =
          restaurantRepository.findRestaurantByRatingCount(1);
      // Redis용 엔티티로 변환
      List<RestaurantForRedis> restaurantForRedisList =
          findRestaurantByRatingCount.stream()
              .map(restaurant -> RestaurantForRedis.toRestaurantForRedis(restaurant))
              .toList();
      // Redis에 저장
      forRedisRepository.saveAll(restaurantForRedisList);
    } catch (Exception e) {
      log.error("평가 수 기준 맛집 목록 업데이트 실패: ", e.getMessage());
    }
  }
}
