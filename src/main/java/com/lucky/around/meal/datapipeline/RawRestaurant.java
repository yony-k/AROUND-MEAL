package com.lucky.around.meal.datapipeline;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawRestaurant {

  @Id private String id;

  @Lob private String jsonData;

  private String hash;

  private boolean isUpdated;

  @Builder
  public RawRestaurant(String id, String jsonData, String hash, boolean isUpdated) {
    this.id = id;
    this.jsonData = jsonData;
    this.hash = hash;
    this.isUpdated = isUpdated;
  }
}
