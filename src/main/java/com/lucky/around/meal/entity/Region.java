package com.lucky.around.meal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Region {
  @Id private Long id;
  @NotNull private String doSi;
  @NotNull private String sgg;
  private double lon;
  private double lat;
}
