package com.lucky.around.meal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import org.locationtech.jts.geom.Point;

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
  @NotNull private String dosi;
  @NotNull private String sigungu;
  private String jibunDetailAddress;
  private String doroDetailAddress;

  @NotNull
  @Enumerated(value = EnumType.STRING)
  private Category category;

  private String restaurantTel;

  @Column(columnDefinition = "geometry(Point, 4326)")
  private Point location;

  private double ratingAverage;

  @Builder
  private Restaurant(
      String id,
      String restaurantName,
      String dosi,
      String sigungu,
      String jibunDetailAddress,
      String doroDetailAddress,
      Category category,
      String restaurantTel,
      Point location) {
    this.id = id;
    this.restaurantName = restaurantName;
    this.dosi = dosi;
    this.sigungu = sigungu;
    this.jibunDetailAddress = jibunDetailAddress;
    this.doroDetailAddress = doroDetailAddress;
    this.category = category;
    this.restaurantTel = restaurantTel;
    this.location = location;
  }

  public String getJibunAddress() {
    return dosi + " " + sigungu + " " + jibunDetailAddress;
  }

  public String getDoroAddress() {
    return dosi + " " + sigungu + " " + doroDetailAddress;
  }
}
