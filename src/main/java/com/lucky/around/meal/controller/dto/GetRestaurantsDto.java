package com.lucky.around.meal.controller.dto;

import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;

public record GetRestaurantsDto(
    String id,
    String restaurantName,
    String jibunAddress,
    String doroAddress,
    Category category,
    String restaurantTel) {
  public static GetRestaurantsDto toDto(Restaurant restaurant) {
    return new GetRestaurantsDto(
        restaurant.getId(),
        restaurant.getRestaurantName(),
        restaurant.getJibunAddress(),
        restaurant.getDoroAddress(),
        restaurant.getCategory(),
        restaurant.getRestaurantTel());
  }
}
