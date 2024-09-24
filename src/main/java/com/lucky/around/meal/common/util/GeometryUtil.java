package com.lucky.around.meal.common.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GeometryUtil {
  private final GeometryFactory geometryFactory;

  public Point createPoint(final double lon, final double lat) {
    Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
    point.setSRID(4326);
    return point;
  }
}
