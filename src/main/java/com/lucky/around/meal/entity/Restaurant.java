package com.lucky.around.meal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import com.lucky.around.meal.entity.enums.Category;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Restaurant {
  @Id private String id;
  @NotNull private String restaurantName;
  @ManyToOne private Region region;
  private String jibunDetailAddress;
  private String doroDetailAddress;

  @NotNull
  @Enumerated(value = EnumType.STRING)
  private Category category;

  private String restaurantTel;
  private double lon;
  private double lat;
  private double ratingAverage;

  @Builder
  private Restaurant(
      String id,
      String restaurantName,
      Region region,
      String jibunDetailAddress,
      String doroDetailAddress,
      Category category,
      String restaurantTel,
      double lon,
      double lat) {
    this.id = id;
    this.restaurantName = restaurantName;
    this.region = region;
    this.jibunDetailAddress = jibunDetailAddress;
    this.doroDetailAddress = doroDetailAddress;
    this.category = category;
    this.restaurantTel = restaurantTel;
    this.lon = lon;
    this.lat = lat;
  }
}
