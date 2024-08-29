package com.lucky.around.meal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.controller.dto.GetRestaurantsDto;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {
  private final RestaurantRepository restaurantRepository;
  private final GeometryUtil geometryUtil;

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
