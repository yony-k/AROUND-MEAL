package com.lucky.around.meal.repository;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lucky.around.meal.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
  @Query(
      "SELECT r FROM Restaurant r "
          + "WHERE ST_DWithin(r.location, :location, :range) "
          + "ORDER BY r.ratingAverage DESC")
  List<Restaurant> findRestaurantsWithinRangeByRating(
      @Param("location") final Point location, @Param("range") final double range);

  @Query(
      "SELECT r FROM Restaurant r "
          + "WHERE ST_DWithin(r.location, :location, :range) "
          + "ORDER BY ST_Distance(r.location, :location) ASC")
  List<Restaurant> findRestaurantsWithinRangeByDistance(
      @Param("location") final Point location, @Param("range") final double range);
}
