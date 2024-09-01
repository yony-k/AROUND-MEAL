package com.lucky.around.meal.controller.record;

import com.lucky.around.meal.entity.Region;

public record RegionRecord(Long id, String dosi, String sigungu, double lon, double lat) {
  public static RegionRecord fromEntity(Region region) {
    return new RegionRecord(
        region.getId(), region.getDosi(), region.getSigungu(), region.getLon(), region.getLat());
  }
}
