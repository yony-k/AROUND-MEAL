package com.lucky.around.meal.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
  KOREAN("한식"),
  CHINESE("중식"),
  JAPANESE("일식"),
  WESTERN("양식"),
  CHICKEN("치킨"),
  PIZZA("피자"),
  FASTFOOD("패스트푸드"),
  CAFE("카페"),
  DESSERT("디저트"),
  SNACK("분식"),
  BAKERY("베이커리"),
  ETC("기타");

  private String name;
}
