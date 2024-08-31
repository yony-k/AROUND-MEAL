package com.lucky.around.meal.common.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;

  public void saveRealTimeLocation(String key, double lat, double lon, Long memberId) {
    GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
    geoOps.add(key, new Point(lat, lon), String.valueOf(memberId));
    redisTemplate.expire(key, 15, TimeUnit.MINUTES);
  }
}
