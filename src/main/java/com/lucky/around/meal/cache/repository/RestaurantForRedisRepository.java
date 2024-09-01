package com.lucky.around.meal.cache.repository;

import org.springframework.data.repository.CrudRepository;

import com.lucky.around.meal.entity.Restaurant;

public interface RestaurantForRedisRepository extends CrudRepository<Restaurant, String> {}
