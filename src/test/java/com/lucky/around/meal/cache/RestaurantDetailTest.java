package com.lucky.around.meal.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lucky.around.meal.cache.repository.RestaurantForRedisRepository;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.repository.RatingRepository;
import com.lucky.around.meal.repository.RestaurantRepository;
import com.lucky.around.meal.service.RestaurantService;

public class RestaurantDetailTest {
  @InjectMocks private RestaurantService restaurantService;

  @Mock private RestaurantRepository restaurantRepository;

  @Mock private RestaurantForRedisRepository restaurantForRedisRepository;

  @Mock private GeometryUtil geometryUtil;

  @Mock private RatingRepository ratingRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /*
  @Test
  @DisplayName("맛집 상세 정보 조회 - Redis 캐시에 데이터가 있는 경우")
  public void getRestaurantDetailFromCacheTest() {
    // given
    String restaurantId = "3220000-101-2023-00376";

    RestaurantForRedis restaurantForRedis =
        RestaurantForRedis.builder()
            .id(restaurantId)
            .restaurantName("Test Restaurant")
            .dosi("Test Dosi")
            .sigungu("Test Sigungu")
            .jibunDetailAddress("Test Address")
            .doroDetailAddress("Test Address")
            .category(Category.CAFE)
            .restaurantTel("Test Tel")
            .lat(203229.463178363)
            .lon(444918.667523901)
            .ratingAverage(4.5)
            .build();

    Restaurant restaurant =
        restaurantForRedis.toRestaurant(
            geometryUtil.createPoint(restaurantForRedis.getLat(), restaurantForRedis.getLon()));

    Member member1 =
        Member.builder()
            .memberId(1L)
            .memberName("Test Member1")
            .email("Test Email1")
            .password("Test Password")
            .role(MemberRole.USER)
            .build();

    Member member2 =
        Member.builder()
            .memberId(2L)
            .memberName("Test Member2")
            .email("Test Email2")
            .password("Test Password")
            .role(MemberRole.USER)
            .build();

    List<Rating> ratings =
        List.of(
            new Rating(member1, restaurant, 5, "Great", null),
            new Rating(member2, restaurant, 4, "Good", null));

    when(restaurantForRedisRepository.findById(restaurantId))
        .thenReturn(Optional.of(restaurantForRedis));
    when(ratingRepository.findByRestaurantIdOrderByCreateAtDesc(restaurantId)).thenReturn(ratings);

    // when
    RestaurantDetailResponseDto result = restaurantService.getRestaurantDetail(restaurantId);

    // then
    assertNotNull(result);
    assertEquals(restaurantId, result.restaurantId());
    assertEquals("Test Restaurant", result.restaurantName());
    assertEquals(2, result.rating().size());
  }


  @Test
  @DisplayName("레디스 변환 테스트")
  public void redisTest() {
    // given
    String restaurantId = "testId";

    RestaurantForRedis restaurantForRedis =
            RestaurantForRedis.builder()
                    .id(restaurantId)
                    .restaurantName("Test Restaurant")
                    .dosi("Test Dosi")
                    .sigungu("Test Sigungu")
                    .jibunDetailAddress("Test Address")
                    .doroDetailAddress("Test Address")
                    .category(Category.CAFE)
                    .restaurantTel("Test Tel")
                    .lat(203229.463178363)
                    .lon(444918.667523901)
                    .ratingAverage(4.5)
                    .build();

    GeometryFactory geometryFactory = new GeometryFactory();
    GeometryUtil geometryUtil = new GeometryUtil(geometryFactory);

    Point location = geometryUtil.createPoint(restaurantForRedis.getLat(),restaurantForRedis.getLon());

    Restaurant restaurant = restaurantForRedis.toRestaurant(location);

    when(restaurantForRedisRepository.findById(restaurantId)).thenReturn(Optional.of(restaurantForRedis));

    // when
    Restaurant result = restaurantService.getRestaurantDetailInRedis(restaurantId);

    // then
    assertNotNull(result);
    assertEquals(restaurantId, result.getId());
    assertEquals("Test Restaurant", result.getRestaurantName());
  }
   */
}
