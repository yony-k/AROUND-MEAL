package com.lucky.around.meal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucky.around.meal.controller.request.RatingRequestDto;
import com.lucky.around.meal.controller.response.RatingResponseDto;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.entity.Rating;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.exception.CustomException;
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

  @Transactional
  public RatingResponseDto createRating(RatingRequestDto requestDto) {

    // todo : 회원 예외처리
    Member member =
        memberRepository
            .findById(requestDto.memberId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    Restaurant restaurant =
        restaurantRepository
            .findById(requestDto.restaurantId())
            .orElseThrow(() -> new CustomException(RestaurantExceptionType.RESTAURANT_NOT_FOUND));

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

    return new RatingResponseDto(
        rating.getId(),
        rating.getMember().getMemberId(),
        rating.getRestaurant().getId(),
        rating.getScore(),
        rating.getContent(),
        rating.getCreateAt());
  }
}
