package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum RegionExceptionType implements ExceptionType {
  REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 지역을 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String message;

  @Override
  public HttpStatus status() {
    return null;
  }

  @Override
  public String message() {
    return null;
  }
}
