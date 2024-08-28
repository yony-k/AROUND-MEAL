package com.lucky.around.meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucky.around.meal.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {}
