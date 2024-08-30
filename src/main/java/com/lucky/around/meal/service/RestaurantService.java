package com.lucky.around.meal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.*;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.RestaurantExceptionType;
import com.lucky.around.meal.repository.RatingRepository;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final GeometryUtil geometryUtil;
  private final RatingRepository ratingRepository;

  // 맛집 상세 정보 조회
  public RestaurantDetailResponseDto getRestaurantDetail(String restaurantId) {
    Restaurant restaurant = findRestaurantById(restaurantId);
    List<RatingResponseDto> ratings = mapRatingsToDto(restaurantId);
    return mapRestaurantToDto(restaurant, ratings);
  }

  // 맛집 ID로 맛집 조회
  private Restaurant findRestaurantById(String restaurantId) {
    return restaurantRepository
        .findById(restaurantId)
        .orElseThrow(() -> new CustomException(RestaurantExceptionType.RESTAURANT_NOT_FOUND));
  }

  // 맛집 ID로 평점 리스트를 DTO 리스트로 변환 -> 최신순 정렬 필요함
  private List<RatingResponseDto> mapRatingsToDto(String restaurantId) {
    return ratingRepository.findByRestaurantIdOrderByCreateAtDesc(restaurantId).stream()
        .map(this::mapRatingToDto)
        .collect(Collectors.toList());
  }

  // Rating 객체를 DTO로 변환
  private RatingResponseDto mapRatingToDto(Rating rating) {
    return new RatingResponseDto(
        rating.getId(),
        rating.getMember().getMemberId(),
        rating.getRestaurant().getId(),
        rating.getScore(),
        rating.getContent(),
        rating.getCreateAt());
  }

  // Restaurant 객체와 평점 리스트를 DTO로 변환
  private RestaurantDetailResponseDto mapRestaurantToDto(
      Restaurant restaurant, List<RatingResponseDto> ratings) {
    return new RestaurantDetailResponseDto(
        restaurant.getId(),
        restaurant.getRestaurantName(),
        restaurant.getDosi(),
        restaurant.getSigungu(),
        restaurant.getJibunDetailAddress(),
        restaurant.getDoroDetailAddress(),
        restaurant.getCategory().name(),
        restaurant.getRestaurantTel(),
        restaurant.getLocation().getX(),
        restaurant.getLocation().getY(),
        restaurant.getRatingAverage(),
        ratings);
  }

  public List<GetRestaurantsDto> getRestaurantsWithinRange(
      final double lat, final double lon, final double range, final String sort) {
    List<Restaurant> restaurants;
    Point location = geometryUtil.createPoint(lat, lon);
    if ("rating".equalsIgnoreCase(sort)) {
      restaurants = restaurantRepository.findRestaurantsWithinRangeByRating(location, range * 1000);
    } else {
      restaurants =
          restaurantRepository.findRestaurantsWithinRangeByDistance(location, range * 1000);
    }

    return restaurants.stream().map(GetRestaurantsDto::toDto).collect(Collectors.toList());
  }
}
