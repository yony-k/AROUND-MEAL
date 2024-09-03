package com.lucky.around.meal.controller;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import com.lucky.around.meal.cache.service.ViewCountService;
import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.controller.response.RestaurantDetailResponseDto;
import com.lucky.around.meal.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

  private final RestaurantService restaurantService;
  private final ViewCountService viewCountService;

  // 맛집 상세 정보 조회
  @GetMapping("/{restaurantId}")
  public ResponseEntity<RestaurantDetailResponseDto> getRestaurantDetail(
      @PathVariable String restaurantId) {
    RestaurantDetailResponseDto restaurantDetail =
        restaurantService.getRestaurantDetail(restaurantId);
    viewCountService.incrementViewCount(restaurantId);
    return ResponseEntity.ok(restaurantDetail);
  }

  // 조회수 기준으로 정렬된 맛집 상세 목록 조회
  @GetMapping("/sorted-by-view-count")
  public ResponseEntity<List<RestaurantDetailResponseDto>> getRestaurantsSortedByViewCount(
      @RequestParam(defaultValue = "desc") String sort) {
    List<RestaurantDetailResponseDto> restaurants =
        restaurantService.getRestaurantsSortedByViewCount(sort);
    return ResponseEntity.ok(restaurants);
  }

  @GetMapping()
  public List<GetRestaurantsDto> findRestaurantsWithinRange(
      final @RequestParam double lat,
      final @RequestParam double lon,
      final @RequestParam double range,
      final @RequestParam(defaultValue = "distance") String sort) {

    return restaurantService.getRestaurantsWithinRange(lat, lon, range, sort);
  }

  // 평가 수 기준 맛집 상세 정보 목록 조회
  @GetMapping("/rating-count")
  public ResponseEntity<List<RestaurantDetailResponseDto>> getRestaurantsByRaitinCount() {
    List<RestaurantDetailResponseDto> restaurants = restaurantService.getRestaurantsByRaitinCount();
    return ResponseEntity.ok(restaurants);
  }
}
