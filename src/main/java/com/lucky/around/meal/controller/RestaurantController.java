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

  // 조회수가 N개 이상인 맛집 상세 정보 조회
  @GetMapping("/high-view-count")
  public ResponseEntity<List<RestaurantDetailResponseDto>> getRestaurantsWithMinViews(
      @RequestParam long minViews) {
    List<RestaurantDetailResponseDto> restaurants =
        restaurantService.getRestaurantsWithMinViews(minViews);
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
}
