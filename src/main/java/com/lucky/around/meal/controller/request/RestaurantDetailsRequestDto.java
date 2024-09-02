package com.lucky.around.meal.controller.request;

public record RestaurantDetailsRequestDto(
    String id,
    String restaurantName,
    String dosi,
    String sigungu,
    String jibunDetailAddress,
    String doroDetailAddress,
    String category,
    String restaurantTel,
    double ratingAverage) {}
