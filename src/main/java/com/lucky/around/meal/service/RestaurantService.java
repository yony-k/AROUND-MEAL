package com.lucky.around.meal.service;

import java.util.*;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.cache.service.RatingCountService;
import com.lucky.around.meal.cache.service.ViewCountService;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.*;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.RestaurantExceptionType;
import com.lucky.around.meal.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final GeometryUtil geometryUtil;
  private final RatingRepository ratingRepository;
  private final ViewCountService viewCountService;
  private final RatingCountService ratingCountService;

  // 캐시에서 맛집 상세 정보를 조회하거나, 캐시가 없으면 데이터베이스에서 조회하여 캐시에 저장
  @Cacheable(value = "restaurantDetail", key = "#restaurantId")
  public RestaurantDetailResponseDto getRestaurantDetail(String restaurantId) {
    return getRestaurantDetailFromDB(restaurantId);
  }

  // 조회수 기준으로 정렬된 맛집 상세 목록 조회
  public List<RestaurantDetailResponseDto> getRestaurantsSortedByViewCount(String sortDirection) {
    List<Restaurant> restaurants = restaurantRepository.findAll();
    List<RestaurantDetailResponseDto> restaurantList = convertToDto(restaurants);
    Map<String, Long> viewCounts = viewCountService.getAllViewCounts();
    return sortRestaurantsByViewCount(restaurantList, viewCounts, sortDirection);
  }

  // 조회수 기준으로 맛집 목록 정렬
  private List<RestaurantDetailResponseDto> sortRestaurantsByViewCount(
      List<RestaurantDetailResponseDto> restaurantDtos,
      Map<String, Long> viewCounts,
      String sortDirection) {

    Comparator<RestaurantDetailResponseDto> comparator =
        Comparator.comparingLong(dto -> viewCounts.getOrDefault(dto.restaurantId(), 0L));

    // 정렬 방향 설정
    if ("desc".equalsIgnoreCase(sortDirection)) {
      comparator = comparator.reversed();
    }

    // 맛집 리스트를 정렬하고 반환
    return restaurantDtos.stream().sorted(comparator).toList();
  }

  // 평가 수 기준 맛집 상세 정보 목록 조회
  public List<RestaurantDetailResponseDto> getRestaurantsByRaitinCount() {
    List<Restaurant> restaurants = ratingCountService.getRaitinCountList();
    List<RestaurantDetailResponseDto> result =
        restaurants.stream()
            .map(
                restaurant -> {
                  List<RatingResponseDto> ratings = mapRatingsToDto(restaurant.getId());
                  return mapRestaurantToDto(restaurant, ratings);
                })
            .toList();
    return result;
  }

  public List<GetRestaurantsDto> getRestaurantsWithinRange(
      final double lat, final double lon, final double range, final String sort) {
    List<Restaurant> restaurants;
    Point location = geometryUtil.createPoint(lon, lat);
    double distanceInMeters = range * 1000;
    boolean isRatingSort = "rating".equalsIgnoreCase(sort);
    boolean isDistanceSort = "distance".equalsIgnoreCase(sort);

    if (isRatingSort) {
      restaurants =
          restaurantRepository.findRestaurantsWithinRangeByRating(location, distanceInMeters);
    } else if (isDistanceSort) {
      restaurants =
          restaurantRepository.findRestaurantsWithinRangeByDistance(location, distanceInMeters);
    } else {
      throw new CustomException(RestaurantExceptionType.INVALID_SORT_TYPE);
    }

    return restaurants.stream().map(GetRestaurantsDto::toDto).collect(Collectors.toList());
  }

  // DB에서 맛집 상세 정보 조회
  public RestaurantDetailResponseDto getRestaurantDetailFromDB(String restaurantId) {
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

  // 맛집 ID로 평점 리스트를 DTO 리스트로 변환
  private List<RatingResponseDto> mapRatingsToDto(String restaurantId) {
    return ratingRepository.findByRestaurantIdOrderByCreateAtDesc(restaurantId).stream()
        .map(this::mapRatingToDto)
        .toList();
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

  // 전체 맛집을 DTO로 변환
  private List<RestaurantDetailResponseDto> convertToDto(List<Restaurant> restaurants) {
    return restaurants.stream().map(this::convertRestaurantToDto).toList();
  }

  // 맛집을 DTO로 변환
  private RestaurantDetailResponseDto convertRestaurantToDto(Restaurant restaurant) {
    List<RatingResponseDto> ratings = mapRatingsToDto(restaurant.getId());
    return mapRestaurantToDto(restaurant, ratings);
  }
}
