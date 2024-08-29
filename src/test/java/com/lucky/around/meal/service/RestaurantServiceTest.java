package com.lucky.around.meal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.*;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.exception.*;
import com.lucky.around.meal.exception.exceptionType.*;
import com.lucky.around.meal.repository.*;

class RestaurantServiceTest {

  private static final String RESTAURANT_ID = "3000000-101-2023-00222";
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

  @Mock private RestaurantRepository restaurantRepository;

  @Mock private RatingRepository ratingRepository;

  @Mock private Member member;

  @InjectMocks private RestaurantService restaurantService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("맛집 상세 정보 조회 성공")
  void getRestaurantDetail_success() {
    // Given
    Restaurant restaurant = createRestaurant();
    Rating rating = createRating(restaurant);
    when(restaurantRepository.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
    when(ratingRepository.findByRestaurantIdOrderByCreateAtDesc(RESTAURANT_ID))
        .thenReturn(Collections.singletonList(rating));

    // When
    RestaurantDetailResponseDto responseDto = restaurantService.getRestaurantDetail(RESTAURANT_ID);

    // Then
    verifyRestaurantDetailResponse(responseDto, rating);
  }

  @Test
  @DisplayName("맛집 상세 정보 조회 실패 - 해당 맛집이 존재하지 않음")
  void getRestaurantDetail_restaurantNotFound() {
    // Given
    when(restaurantRepository.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

    // When & Then
    CustomException thrown =
        assertThrows(
            CustomException.class, () -> restaurantService.getRestaurantDetail(RESTAURANT_ID));
    assertEquals(RestaurantExceptionType.RESTAURANT_NOT_FOUND, thrown.getExceptionType());
  }

  private Restaurant createRestaurant() {
    return Restaurant.builder()
        .id(RESTAURANT_ID)
        .restaurantName(RESTAURANT_NAME)
        .dosi(DOSI)
        .sigungu(SIGUNGU)
        .jibunDetailAddress(JIBUN_ADDRESS)
        .doroDetailAddress(DORO_ADDRESS)
        .category(Category.FAMILY_RESTAURANT)
        .restaurantTel(RESTAURANT_TEL)
        .lon(LON)
        .lat(LAT)
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
    assertEquals(RESTAURANT_ID, responseDto.restaurantId());
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
