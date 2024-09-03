package com.lucky.around.meal.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.Point;
import org.mockito.*;
import org.springframework.data.redis.core.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.controller.response.RestaurantDetailResponseDto;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.repository.RestaurantRepository;

class ViewCountServiceTest {

  private static final String VIEW_COUNT_KEY_PREFIX = "viewCount:";
  private static final String HOURLY_VIEW_COUNT_KEY_PREFIX = "hourlyViewCount:";
  private static final String RESTAURANT_DETAIL_KEY_PREFIX = "restaurantDetail::";
  private static final String RESTAURANT_ID = "300000-12-000465";
  private static final String OTHER_RESTAURANT_ID = "300000-23-0000567";

  @Mock private StringRedisTemplate redisTemplate;
  @Mock private RestaurantRepository restaurantRepository;
  @Mock private ObjectMapper objectMapper;
  @InjectMocks private ViewCountService viewCountService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("특정 맛집의 전체 및 시간별 조회수 증가")
  void testIncrementViewCount() {
    // Given
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);

    // When
    viewCountService.incrementViewCount(RESTAURANT_ID);

    // Then
    verify(valueOps, times(1)).increment(eq(VIEW_COUNT_KEY_PREFIX + RESTAURANT_ID), eq(1L));
    verify(valueOps, times(1)).increment(eq(HOURLY_VIEW_COUNT_KEY_PREFIX + RESTAURANT_ID), eq(1L));
  }

  @Test
  @DisplayName("조회수 데이터가 존재하지 않을 때 : 조회수 0")
  void testGetViewCountWhenValueDoesNotExist() {
    // Given
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.get(eq(VIEW_COUNT_KEY_PREFIX + OTHER_RESTAURANT_ID))).thenReturn(null);

    // When
    Long viewCount = viewCountService.getViewCount(OTHER_RESTAURANT_ID);

    // Then
    assertEquals(0L, viewCount);
  }

  @Test
  @DisplayName("조회수 데이터가 존재할 때 : 전체 조회수를 조회")
  void testGetViewCountWhenValueExists() {
    // Given
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.get(eq(VIEW_COUNT_KEY_PREFIX + OTHER_RESTAURANT_ID))).thenReturn("10");

    // When
    Long viewCount = viewCountService.getViewCount(OTHER_RESTAURANT_ID);

    // Then
    assertEquals(10L, viewCount);
  }

  @Test
  @DisplayName("1시간 기준 조회수 N개 이상 캐싱 처리")
  void testUpdateHourlyViewCountCache() throws JsonProcessingException {
    // Given
    Restaurant restaurant = createRestaurant();
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);

    Set<String> hourlyKeys =
        Set.of(
            HOURLY_VIEW_COUNT_KEY_PREFIX + RESTAURANT_ID,
            HOURLY_VIEW_COUNT_KEY_PREFIX + OTHER_RESTAURANT_ID);
    when(redisTemplate.keys(HOURLY_VIEW_COUNT_KEY_PREFIX + "*")).thenReturn(hourlyKeys);
    when(valueOps.get(HOURLY_VIEW_COUNT_KEY_PREFIX + RESTAURANT_ID)).thenReturn("15");
    when(valueOps.get(HOURLY_VIEW_COUNT_KEY_PREFIX + OTHER_RESTAURANT_ID)).thenReturn("5");

    when(restaurantRepository.findById(RESTAURANT_ID))
        .thenReturn(java.util.Optional.of(restaurant));
    when(objectMapper.writeValueAsString(any(RestaurantDetailResponseDto.class)))
        .thenReturn("json");

    // When
    viewCountService.updateHourlyViewCountCache();

    // Then
    verify(valueOps, never()).set(RESTAURANT_DETAIL_KEY_PREFIX + OTHER_RESTAURANT_ID, "json");
  }

  @Test
  @DisplayName("1시간 기준 조회수 초기화")
  void testResetViewCounts() {
    // Given
    Set<String> keys =
        Set.of(HOURLY_VIEW_COUNT_KEY_PREFIX + "123", HOURLY_VIEW_COUNT_KEY_PREFIX + "456");
    when(redisTemplate.keys(HOURLY_VIEW_COUNT_KEY_PREFIX + "*")).thenReturn(keys);
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);

    // When
    viewCountService.resetViewCounts();

    // Then
    verify(valueOps, times(2)).set(anyString(), eq("0"));
  }

  private Restaurant createRestaurant() {
    Point location = mock(Point.class);
    when(location.getX()).thenReturn(12.1010);
    when(location.getY()).thenReturn(11.6666);

    return Restaurant.builder()
        .id(RESTAURANT_ID)
        .restaurantName("Test")
        .dosi("서울")
        .sigungu("중구")
        .jibunDetailAddress("Test 주소")
        .doroDetailAddress("Test 도로명")
        .category(Category.FAMILY_RESTAURANT)
        .restaurantTel("010-2222-5555")
        .ratingAverage(5.0)
        .location(location)
        .build();
  }
}
