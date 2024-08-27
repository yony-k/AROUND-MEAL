package com.lucky.around.meal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Region {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String doSi;
  @NotNull private String sgg;
  private double lon;
  private double lat;

  @Builder
  private Region(String doSi, String sgg, double lon, double lat) {
    this.doSi = doSi;
    this.sgg = sgg;
    this.lon = lon;
    this.lat = lat;
  }
}
