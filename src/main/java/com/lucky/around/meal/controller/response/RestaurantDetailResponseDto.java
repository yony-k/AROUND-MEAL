package com.lucky.around.meal.controller.response;

import java.util.List;

public record RestaurantDetailResponseDto(
    String restaurantId,
    String restaurantName,
    Long region,
    String jibunDetailAddress,
    String doroDetailAddress,
    String category,
    String restaurantTel,
    double lon,
    double lat,
    // double ratingAverage,
    List<RatingResponseDto> rating) {}
