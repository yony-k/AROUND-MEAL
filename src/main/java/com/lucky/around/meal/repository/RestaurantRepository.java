package com.lucky.around.meal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lucky.around.meal.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
  @Query(
      "SELECT r FROM Restaurant r "
          + "WHERE ST_Distance(ST_SetSRID(ST_MakePoint(r.lon, r.lat), 4326), ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) <= :range "
          + "ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(r.lon, r.lat), 4326), ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) ASC")
  List<Restaurant> findRestaurantsWithinRangeByDistance(
      @Param("lat") double lat, @Param("lon") double lon, @Param("range") double range);

  @Query(
      "SELECT r FROM Restaurant r "
          + "WHERE ST_Distance(ST_SetSRID(ST_MakePoint(r.lon, r.lat), 4326), ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) <= :range "
          + "ORDER BY r.ratingAverage DESC")
  List<Restaurant> findRestaurantsWithinRangeByRating(
      @Param("lat") double lat, @Param("lon") double lon, @Param("range") double range);
}
