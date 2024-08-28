package com.lucky.around.meal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Region {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String dosi;
  @NotNull private String sigungu;
  private double lon;
  private double lat;

  @Builder
  private Region(String dosi, String sigungu, double lon, double lat) {
    this.dosi = dosi;
    this.sigungu = sigungu;
    this.lon = lon;
    this.lat = lat;
  }
}
