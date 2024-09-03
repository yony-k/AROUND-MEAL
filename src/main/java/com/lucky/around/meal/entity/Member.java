package com.lucky.around.meal.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.*;

import com.lucky.around.meal.entity.enums.MemberRole;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(nullable = false)
  private String memberName;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  private MemberRole role;

  @Column(nullable = true)
  private double lon;

  @Column(nullable = true)
  private double lat;

  private boolean recommendationAlertsEnabled;

  public void updateRecommendationAlertEnabled(boolean recommendationAlertsEnabled) {
    this.recommendationAlertsEnabled = recommendationAlertsEnabled;
  }

  public void updateLocation(double lon, double lat) {
    this.lon = round(lon, 6);
    this.lat = round(lat, 6);
  }

  private double round(double value, int decimalPlaces) {
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
