package com.lucky.around.meal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
  private final RestaurantService restaurantService;

  @GetMapping
  public List<GetRestaurantsDto> findRestaurantsWithinRange(
      final @RequestParam double lat,
      final @RequestParam double lon,
      final @RequestParam double range,
      final @RequestParam(defaultValue = "distance") String sort) {

    return restaurantService.getRestaurants(lat, lon, range, sort);
  }
}
