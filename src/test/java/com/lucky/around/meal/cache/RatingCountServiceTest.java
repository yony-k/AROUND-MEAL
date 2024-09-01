package com.lucky.around.meal.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lucky.around.meal.cache.entity.RestaurantForRedis;
import com.lucky.around.meal.cache.repository.RestaurantForRedisRepository;
import com.lucky.around.meal.cache.service.RatingCountService;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.repository.RestaurantRepository;

public class RatingCountServiceTest {

  @InjectMocks private RatingCountService ratingCountService;

  @Mock private RestaurantRepository restaurantRepository;

  @Mock private RestaurantForRedisRepository forRedisRepository;

  @BeforeEach
  public void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("평가 수 기준 맛집 자동 업데이트 테스트")
  public void cachingByRatingCountTest() {
    // given

    GeometryUtil geometryUtil = new GeometryUtil(new GeometryFactory());

    Restaurant expactedRestaurant =
        Restaurant.builder()
            .id("testId")
            .restaurantName("testRestaurantName")
            .dosi("testDosi")
            .sigungu("testSigungu")
            .jibunDetailAddress("역삼동 657-40")
            .doroDetailAddress("봉은사로34길 28, 지상1층 (역삼동)")
            .category(Category.CAFE)
            .restaurantTel("testTel")
            .location(geometryUtil.createPoint(127.0, 37.0))
            .ratingAverage(0)
            .build();

    List<Restaurant> expactedList = List.of(expactedRestaurant);

    when(restaurantRepository.findRestaurantByRatingCount(1)).thenReturn(expactedList);

    RestaurantForRedis expectedForRedis =
        RestaurantForRedis.toRestaurantForRedis(expactedList.get(0));
    List<RestaurantForRedis> expectedForRedisList = List.of(expectedForRedis);

    // when
    assertDoesNotThrow(() -> ratingCountService.cachingByRatingCount());

    // then
    verify(forRedisRepository, times(1)).saveAll(anyList());
  }
}
