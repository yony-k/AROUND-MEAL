package com.lucky.around.meal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lucky.around.meal.cache.service.RatingCountService;
import com.lucky.around.meal.controller.request.RatingRequestDto;
import com.lucky.around.meal.controller.response.RatingResponseDto;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.entity.Rating;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.RatingException;
import com.lucky.around.meal.exception.exceptionType.RestaurantExceptionType;
import com.lucky.around.meal.repository.MemberRepository;
import com.lucky.around.meal.repository.RatingRepository;
import com.lucky.around.meal.repository.RestaurantRepository;

public class RatingServiceTest {

  @Mock private RatingRepository ratingRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private RestaurantRepository restaurantRepository;
  @Mock private RatingCountService ratingCountService;

  @InjectMocks private RatingService ratingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("평가 생성 성공")
  public void createRatingSuccess() {

    // Given
    Long memberId = 1L;
    String restaurantId = "333-3333";

    Member member = Member.builder().memberId(memberId).build();

    Restaurant restaurant = Restaurant.builder().id(restaurantId).build();

    RatingRequestDto requestDto = new RatingRequestDto(restaurantId, 4, "맛있어요");

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
    when(ratingRepository.findByMemberAndRestaurant(memberId, restaurantId))
        .thenReturn(Optional.empty());

    // 평점 평균 계산 로직 검증
    doReturn((5.0 + 4.0) / 2).when(ratingRepository).findAvgScoreByRestaurantId(restaurantId);

    // When
    RatingResponseDto responseDto = ratingService.createRating(memberId, requestDto);

    // Then
    assertNotNull(responseDto);
    assertEquals(4, responseDto.score());
    assertEquals("맛있어요", responseDto.content());

    Double avgScoreByRestaurantId = ratingRepository.findAvgScoreByRestaurantId(restaurantId);
    assertEquals(4.5, avgScoreByRestaurantId);
    verify(ratingCountService, times(1)).updateRating(restaurant.getId(), "ratingAverage", 4.5);
    verify(ratingRepository, times(1)).save(any(Rating.class));
    verify(restaurantRepository, times(1)).save(restaurant);
  }

  @Test
  @DisplayName("존재하지 않는 식당 검증")
  void restaurantNotFound() {

    // Given
    Member member = Member.builder().memberId(1L).build();
    RatingRequestDto ratingRequestDto = new RatingRequestDto("333-3333", 5, "또 와야지");

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    when(restaurantRepository.findById("333-3333")).thenReturn(Optional.empty()); // empty() 설정

    // When & Then 존재하지 않는 식당 예외 메세지
    CustomException exception =
        assertThrows(CustomException.class, () -> ratingService.createRating(1L, ratingRequestDto));
    assertEquals(RestaurantExceptionType.RESTAURANT_NOT_FOUND, exception.getExceptionType());

    // 메서드 호출 X 확인
    verify(ratingRepository, never()).save(any(Rating.class));
  }

  @Test
  @DisplayName("평가 중복 예외처리 검증")
  void createRatingDuplicate() {

    // given
    Member member = Member.builder().memberId(1L).build();
    Restaurant restaurant = Restaurant.builder().id("333-3333").build();
    RatingRequestDto ratingRequestDto = new RatingRequestDto("333-3333", 5, "또 와야지");

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    when(restaurantRepository.findById("333-3333")).thenReturn(Optional.of(restaurant));
    when(ratingRepository.findByMemberAndRestaurant(1L, "333-3333"))
        .thenReturn(
            Optional.of(
                Rating.builder()
                    .member(member)
                    .restaurant(restaurant)
                    .score(4)
                    .content("짱맛있네요")
                    .build()));

    // When & Then 이미 평가한 식당 예외 메세지
    CustomException exception =
        assertThrows(CustomException.class, () -> ratingService.createRating(1L, ratingRequestDto));
    assertEquals(RatingException.ALREADY_RATED, exception.getExceptionType());

    // 메서드 호출 X 확인
    verify(ratingRepository, never()).save(any(Rating.class));
  }
}
