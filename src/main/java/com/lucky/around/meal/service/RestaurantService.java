package com.lucky.around.meal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {
  private final RestaurantRepository restaurantRepository;

  public List<GetRestaurantsDto> getRestaurants(
      final double lat, final double lon, final double range, final String sort) {
    List<Restaurant> restaurants;

    if ("rating".equalsIgnoreCase(sort)) {
      restaurants = restaurantRepository.findRestaurantsWithinRangeByRating(lat, lon, range * 1000);
    } else {
      restaurants =
          restaurantRepository.findRestaurantsWithinRangeByDistance(lat, lon, range * 1000);
    }

    return restaurants.stream().map(GetRestaurantsDto::toDto).collect(Collectors.toList());
  }
}
