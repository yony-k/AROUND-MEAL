package com.lucky.around.meal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.controller.response.RestaurantDetailResponseDto;
import com.lucky.around.meal.repository.RestaurantRepository;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

  private final RestaurantService restaurantService;
  private final RestaurantRepository restaurantRepository;

  // 맛집 상세 정보 조회
  @GetMapping("/{restaurantId}")
  public ResponseEntity<RestaurantDetailResponseDto> getRestaurantDetail(
      @PathVariable String restaurantId) {
    RestaurantDetailResponseDto restaurantDetail =
        restaurantService.getRestaurantDetail(restaurantId);
    return ResponseEntity.ok(restaurantDetail);

  @GetMapping
  public List<GetRestaurantsDto> findRestaurantsWithinRange(
      final @RequestParam double lat,
      final @RequestParam double lon,
      final @RequestParam double range,
      final @RequestParam(defaultValue = "distance") String sort) {

    return restaurantService.getRestaurantsWithinRange(lat, lon, range, sort);
  }
}
