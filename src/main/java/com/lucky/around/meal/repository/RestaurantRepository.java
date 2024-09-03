package com.lucky.around.meal.repository;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lucky.around.meal.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

  @Query(
      "SELECT r FROM Restaurant r "
          + "WHERE ST_Distance(ST_Transform(r.location, 4326), ST_Transform(:location, 4326)) <= :range "
          + "ORDER BY r.ratingAverage DESC")
  List<Restaurant> findRestaurantsWithinRangeByRating(
      @Param("location") final Point location, @Param("range") final double range);

  @Query(
      "SELECT r FROM Restaurant r "
          + "WHERE ST_Distance(ST_Transform(r.location, 4326), ST_Transform(:location, 4326)) <= :range "
          + "ORDER BY ST_Distance(ST_Transform(r.location, 4326), ST_Transform(:location, 4326)) ASC")
  List<Restaurant> findRestaurantsWithinRangeByDistance(
      @Param("location") final Point location, @Param("range") final double range);

  @Query(
      value =
          "SELECT r.* FROM restaurant r "
              + "JOIN rating r2 ON r.id = r2.restaurant_id "
              + "GROUP BY r.id HAVING COUNT(r2.id) >= :count",
      nativeQuery = true)
  List<Restaurant> findRestaurantByRatingCount(@Param("count") final int count);

  @Query(
      "select r from Restaurant r "
          + "where ST_Distance("
          + "    ST_Transform(ST_SetSRID(r.location, 4326), 4326), "
          + "    ST_Transform(ST_SetSRID(:memberLocation, 4326), 4326)"
          + ") <= 1000 "
          + "and r.ratingAverage > 0 "
          + "order by function('RANDOM') "
          + "limit 1")
  Optional<Restaurant> findRecommendedRestaurantForMember(Point memberLocation);
}
