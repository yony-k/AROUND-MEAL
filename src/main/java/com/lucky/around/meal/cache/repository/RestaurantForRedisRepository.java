package com.lucky.around.meal.cache.repository;

import org.springframework.data.repository.CrudRepository;

import com.lucky.around.meal.cache.entity.RestaurantForRedis;

public interface RestaurantForRedisRepository extends CrudRepository<RestaurantForRedis, String> {}
