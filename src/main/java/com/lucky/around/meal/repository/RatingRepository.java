package com.lucky.around.meal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lucky.around.meal.entity.Rating;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

  List<Rating> findByRestaurantIdOrderByCreateAtDesc(String restaurantId);

  @Query("SELECT AVG(r.score) FROM Rating r WHERE r.restaurant.id = :restaurantId")
  Double findAvgScoreByRestaurantId(@Param("restaurantId") String restaurantId);

  @Query("SELECT r FROM Rating r WHERE r.member.id = :memberId AND r.restaurant.id = :restaurantId")
  Optional<Rating> findByMemberAndRestaurant(
      @Param("memberId") Long memberId, @Param("restaurantId") String restaurantId);
}
