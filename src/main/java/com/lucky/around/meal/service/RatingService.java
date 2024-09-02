package com.lucky.around.meal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucky.around.meal.cache.service.RatingCountService;
import com.lucky.around.meal.controller.request.RatingRequestDto;
import com.lucky.around.meal.controller.response.RatingResponseDto;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.entity.Rating;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.MemberExceptionType;
import com.lucky.around.meal.exception.exceptionType.RatingException;
import com.lucky.around.meal.exception.exceptionType.RestaurantExceptionType;
import com.lucky.around.meal.repository.MemberRepository;
import com.lucky.around.meal.repository.RatingRepository;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {

  private final RatingRepository ratingRepository;
  private final MemberRepository memberRepository;
  private final RestaurantRepository restaurantRepository;
  private final RatingCountService ratingCountService;

  @Transactional
  public RatingResponseDto createRating(Long memberId, RatingRequestDto requestDto) {

    Member member =
        memberRepository
            .findById(memberId)
            .orElseThrow(() -> new CustomException(MemberExceptionType.MEMBER_NOT_FOUND));

    Restaurant restaurant =
        restaurantRepository
            .findById(requestDto.restaurantId())
            .orElseThrow(() -> new CustomException(RestaurantExceptionType.RESTAURANT_NOT_FOUND));

    // 중복 평가 방지
    ratingRepository
        .findByMemberAndRestaurant(memberId, requestDto.restaurantId())
        .ifPresent(
            existingRating -> {
              throw new CustomException(RatingException.ALREADY_RATED);
            });

    Rating rating =
        Rating.builder()
            .member(member)
            .restaurant(restaurant)
            .score(requestDto.score())
            .content(requestDto.content())
            .build();

    ratingRepository.save(rating);

    // 식당 평균 score 계산
    Double ratingAverage = ratingRepository.findAvgScoreByRestaurantId(requestDto.restaurantId());

    restaurant.updateRatingAverage(ratingAverage); // 평균 변화

    restaurantRepository.save(restaurant); // 저장

    // Redis에 등록된 평가 수 기준 맛집 목록 속 맛집의 식당 평균 갱신
    ratingCountService.updateRating(restaurant.getId(), "ratingAverage", ratingAverage);

    return new RatingResponseDto(
        rating.getId(),
        rating.getMember().getMemberId(),
        rating.getRestaurant().getId(),
        rating.getScore(),
        rating.getContent(),
        rating.getCreateAt());
  }
}
