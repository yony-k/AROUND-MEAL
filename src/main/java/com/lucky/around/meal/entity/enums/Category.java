package com.lucky.around.meal.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
  KOREAN_FOOD("한식"),
  JAPANESE_FOOD("일식"),
  CHINESE_FOOD("중국식"),
  FAMILY_RESTAURANT("패밀리레스토랑"),
  CHICKEN_AND_BEER("호프/통닭"),
  SOJU_BAR("정종/대포집/소주방"),
  BBQ("식육(숯불구이)"),
  INTERNATIONAL_CUISINE("외국음식전문점(인도,태국등)"),
  SNACK_BAR("분식"),
  SENSUAL_BAR("감성주점"),
  SUSHI_BAR("횟집"),
  KIDS_CAFE("키즈카페"),
  WESTERN_FOOD("경양식"),
  LIVE_CAFE("라이브카페"),
  BUFFET("뷔페식"),
  CAFE("까페"),
  KIMBAP("김밥(도시락)"),
  COLD_NOODLE("냉면집"),
  CATERING("출장조리"),
  ETC("기타");

  private String name;
}
