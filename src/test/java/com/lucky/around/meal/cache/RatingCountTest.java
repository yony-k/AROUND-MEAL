package com.lucky.around.meal.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.lucky.around.meal.cache.service.RatingCountService;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.repository.RestaurantRepository;

public class RatingCountTest {

  @InjectMocks private RatingCountService ratingCountService;

  @Mock private RestaurantRepository restaurantRepository;

  @Mock private RedisTemplate<String, Object> redisTemplate;

  @Mock private HashOperations<String, Object, Object> hashOperations;

  @Mock private GeometryUtil geometryUtil;

  private static final String CACHE_KEY_PREFIX = "count:";

  @BeforeEach
  public void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(ratingCountService, "ratingNumber", 1);
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);
  }

  @Test
  @DisplayName("평가 수 기준 맛집 업로드 테스트")
  public void cachingByRatingCountTest() {
    // given

    // Point 변환을 위한 클래스
    GeometryUtil geometryUtil = new GeometryUtil(new GeometryFactory());

    // 비교용 객체 생성
    Restaurant restaurant =
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

    List<Restaurant> expactedList = List.of(restaurant);

    // DB에서 리스트 가져올 때 반환값 위의 리스트로 지정
    when(restaurantRepository.findRestaurantByRatingCount(1)).thenReturn(expactedList);

    // 메소드 내에서 만들어지는 해시맵과 비교하기위한 객체 생성
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

    // when
    assertDoesNotThrow(() -> ratingCountService.cachingByRatingCount());

    // then
    // 매개변수를 잡기 위한 ArgumentCaptor 설정
    ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Map<String, Object>> hashCaptor = ArgumentCaptor.forClass(Map.class);

    //
    verify(hashOperations).putAll(keyCaptor.capture(), hashCaptor.capture());

    String capturedKey = keyCaptor.getValue();
    Map<String, Object> capturedHash = hashCaptor.getValue();

    assertEquals(key, capturedKey);
    assertEquals(hash, capturedHash);
  }

  @Test
  @DisplayName("평가 수 기준 맛집 목록 조회")
  public void getRaitinCountListTest() {
    // given
    // Point 변환을 위한 클래스
    GeometryUtil geometryUtil2 = new GeometryUtil(new GeometryFactory());

    // 비교용 객체 생성
    Restaurant restaurant =
        Restaurant.builder()
            .id("testId")
            .restaurantName("testRestaurantName")
            .dosi("testDosi")
            .sigungu("testSigungu")
            .jibunDetailAddress("역삼동 657-40")
            .doroDetailAddress("봉은사로34길 28, 지상1층 (역삼동)")
            .category(Category.CAFE)
            .restaurantTel("testTel")
            .location(geometryUtil2.createPoint(127.0, 37.0))
            .ratingAverage(0)
            .build();

    List<Restaurant> expactedList = List.of(restaurant);

    // 레디스에서 값을 가져올 때 사용될 키
    String key = CACHE_KEY_PREFIX + restaurant.getId();

    Point location = geometryUtil2.createPoint(127.0, 37.0);

    // 메소드 내에서 만들어지는 해시맵과 비교하기위한 객체 생성
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

    Map<Object, Object> objectMap = new HashMap<>(hash);

    when(redisTemplate.keys(CACHE_KEY_PREFIX + "*")).thenReturn(Set.of(key));
    when(redisTemplate.opsForHash().entries(key)).thenReturn(objectMap);
    when(geometryUtil.createPoint(
            Double.parseDouble(String.valueOf(objectMap.get("lon"))),
            Double.parseDouble(String.valueOf(objectMap.get("lat")))))
        .thenReturn(location);

    // when
    List<Restaurant> result = ratingCountService.getRaitinCountList();

    // then
    assertEquals(expactedList.size(), result.size(), "리스트 크기가 다릅니다.");
    for (int i = 0; i < expactedList.size(); i++) {
      Restaurant expected = expactedList.get(i);
      Restaurant actual = result.get(i);

      assertEquals(expected.getId(), actual.getId(), "ID가 다릅니다.");
      assertEquals(
          expected.getRestaurantName(), actual.getRestaurantName(), "RestaurantName이 다릅니다.");
      assertEquals(expected.getDosi(), actual.getDosi(), "Dosi가 다릅니다.");
      assertEquals(expected.getSigungu(), actual.getSigungu(), "Sigungu가 다릅니다.");
      assertEquals(
          expected.getJibunDetailAddress(),
          actual.getJibunDetailAddress(),
          "JibunDetailAddress가 다릅니다.");
      assertEquals(
          expected.getDoroDetailAddress(),
          actual.getDoroDetailAddress(),
          "DoroDetailAddress가 다릅니다.");
      assertEquals(expected.getCategory(), actual.getCategory(), "Category가 다릅니다.");
      assertEquals(expected.getRestaurantTel(), actual.getRestaurantTel(), "RestaurantTel이 다릅니다.");
      assertEquals(expected.getLocation(), actual.getLocation(), "Location이 다릅니다.");
      assertEquals(expected.getRatingAverage(), actual.getRatingAverage(), "RatingAverage가 다릅니다.");
    }
  }
}
