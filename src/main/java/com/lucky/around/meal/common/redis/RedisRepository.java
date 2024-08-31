package com.lucky.around.meal.common.redis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.MemberExceptionType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;

  public void saveRealTimeLocation(String key, double lon, double lat, Long memberId) {
    GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
    geoOps.add(key, new Point(round(lon, 6), round(lat, 6)), String.valueOf(memberId));
    redisTemplate.expire(key, 15, TimeUnit.MINUTES);
  }

  public Point getRealTimeLocation(String key, Long memberId) {
    GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
    List<Point> positions = geoOps.position(key, String.valueOf(memberId));
    Point point = getFirstPositionOrThrow(positions);
    return new Point(round(point.getX(), 6), round(point.getY(), 6));
  }

  private Point getFirstPositionOrThrow(List<Point> positions) {
    return Optional.ofNullable(positions)
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(0))
        .orElseThrow(() -> new CustomException(MemberExceptionType.REALTIME_LOCATION_NOT_FOUND));
  }

  private double round(double value, int decimalPlaces) {
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
