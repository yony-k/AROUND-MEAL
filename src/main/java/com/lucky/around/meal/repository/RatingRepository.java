package com.lucky.around.meal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lucky.around.meal.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

  List<Rating> findByRestaurantIdOrderByCreateAtDesc(String restaurantId);
}
