package com.lucky.around.meal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.*;
import org.mockito.*;

import com.lucky.around.meal.cache.service.ViewCountService;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.*;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.exception.*;
import com.lucky.around.meal.exception.exceptionType.*;
import com.lucky.around.meal.repository.*;

class RestaurantServiceTest {

  private static final String RESTAURANT_ID_1 = "3000000-101-2023-00221";
  private static final String RESTAURANT_ID_2 = "3000000-101-2023-00222";
  private static final String RESTAURANT_ID_3 = "3000000-101-2023-00223";
  private static final String RESTAURANT_NAME = "Test Restaurant";
  private static final String DOSI = "서울특별시";
  private static final String SIGUNGU = "중구";
  private static final String JIBUN_ADDRESS = "테스트 지번 주소";
  private static final String DORO_ADDRESS = "테스트 도로번 주소";
  private static final String RESTAURANT_TEL = "123-456-7890";
  private static final double LON = 123.456;
  private static final double LAT = 78.910;
  private static final String RATING_CONTENT = "5점 만점";
  private static final int RATING_SCORE = 5;
  private static final double RATING_AVERAGE = 5.0;

  @InjectMocks private RestaurantService restaurantService;

  @Mock private RestaurantRepository restaurantRepository;

  @Mock private RatingRepository ratingRepository;

  @Mock private ViewCountService viewCountService;

  @Mock private GeometryUtil geometryUtil;

  @Mock private Member member;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("맛집 상세 정보 조회 성공")
  void getRestaurantDetail_success() {
    // Given
    GeometryFactory geometryFactory = new GeometryFactory();
    Point mockLocation = geometryFactory.createPoint(new Coordinate(LON, LAT));
    when(geometryUtil.createPoint(LON, LAT)).thenReturn(mockLocation);
    Restaurant restaurant = createRestaurant(RESTAURANT_ID_1);
    Rating rating = createRating(restaurant);
    when(restaurantRepository.findById(RESTAURANT_ID_1)).thenReturn(Optional.of(restaurant));
    when(ratingRepository.findByRestaurantIdOrderByCreateAtDesc(RESTAURANT_ID_1))
        .thenReturn(Collections.singletonList(rating));

    // When
    RestaurantDetailResponseDto responseDto =
        restaurantService.getRestaurantDetail(RESTAURANT_ID_1);

    // Then
    verifyRestaurantDetailResponse(responseDto, rating);
  }

  @Test
  @DisplayName("맛집 상세 정보 조회 실패 - 해당 맛집이 존재하지 않음")
  void getRestaurantDetail_restaurantNotFound() {
    // Given
    when(restaurantRepository.findById(RESTAURANT_ID_1)).thenReturn(Optional.empty());

    // When & Then
    CustomException thrown =
        assertThrows(
            CustomException.class, () -> restaurantService.getRestaurantDetail(RESTAURANT_ID_1));
    assertEquals(RestaurantExceptionType.RESTAURANT_NOT_FOUND, thrown.getExceptionType());
  }

  @Test
  @DisplayName("조회수 기준으로 맛집 상세 목록 정렬 성공")
  void getRestaurantsSortedByViewCount_success() {
    // Given
    Restaurant restaurant1 = createRestaurant(RESTAURANT_ID_1);
    Restaurant restaurant2 = createRestaurant(RESTAURANT_ID_2);
    Restaurant restaurant3 = createRestaurant(RESTAURANT_ID_3);

    Map<String, Long> viewCounts =
        Map.of(
            RESTAURANT_ID_1, 10L,
            RESTAURANT_ID_2, 30L,
            RESTAURANT_ID_3, 20L);

    List<Restaurant> restaurants = List.of(restaurant1, restaurant2, restaurant3);
    when(restaurantRepository.findAll()).thenReturn(restaurants);
    when(viewCountService.getAllViewCounts()).thenReturn(viewCounts);

    // When
    List<RestaurantDetailResponseDto> sortedRestaurants =
        restaurantService.getRestaurantsSortedByViewCount("desc");

    // Then
    assertEquals(3, sortedRestaurants.size());
    assertEquals(RESTAURANT_ID_2, sortedRestaurants.get(0).restaurantId());
    assertEquals(RESTAURANT_ID_3, sortedRestaurants.get(1).restaurantId());
    assertEquals(RESTAURANT_ID_1, sortedRestaurants.get(2).restaurantId());
  }

  private Restaurant createRestaurant(String id) {
    Point location = mock(Point.class);
    when(location.getX()).thenReturn(LON);
    when(location.getY()).thenReturn(LAT);

    return Restaurant.builder()
        .id(id)
        .restaurantName(RESTAURANT_NAME)
        .dosi(DOSI)
        .sigungu(SIGUNGU)
        .jibunDetailAddress(JIBUN_ADDRESS)
        .doroDetailAddress(DORO_ADDRESS)
        .category(Category.FAMILY_RESTAURANT)
        .restaurantTel(RESTAURANT_TEL)
        .ratingAverage(RATING_AVERAGE)
        .location(location)
        .build();
  }

  private Rating createRating(Restaurant restaurant) {
    return Rating.builder()
        .member(member)
        .restaurant(restaurant)
        .score(RATING_SCORE)
        .content(RATING_CONTENT)
        .createAt(LocalDateTime.now())
        .build();
  }

  private void verifyRestaurantDetailResponse(
      RestaurantDetailResponseDto responseDto, Rating rating) {
    assertNotNull(responseDto);
    assertEquals(RESTAURANT_ID_1, responseDto.restaurantId());
    assertEquals(RESTAURANT_NAME, responseDto.restaurantName());
    assertEquals(DOSI, responseDto.dosi());
    assertEquals(SIGUNGU, responseDto.sigungu());
    assertEquals(JIBUN_ADDRESS, responseDto.jibunDetailAddress());
    assertEquals(DORO_ADDRESS, responseDto.doroDetailAddress());
    assertEquals(RESTAURANT_TEL, responseDto.restaurantTel());
    assertEquals(LON, responseDto.lon());
    assertEquals(LAT, responseDto.lat());
    assertEquals(Collections.singletonList(createRatingResponseDto(rating)), responseDto.rating());
  }

  private RatingResponseDto createRatingResponseDto(Rating rating) {
    return new RatingResponseDto(
        rating.getId(),
        member.getMemberId(),
        rating.getRestaurant().getId(),
        rating.getScore(),
        rating.getContent(),
        rating.getCreateAt());
  }
}
