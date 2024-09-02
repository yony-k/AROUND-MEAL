package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum RatingException implements ExceptionType {
  ALREADY_RATED(HttpStatus.CONFLICT, "이미 평가를 남기셨습니다. 중복 평가는 불가능합니다.");

  private final HttpStatus status;
  private final String message;

  @Override
  public HttpStatus status() {
    return this.status;
  }

  @Override
  public String message() {
    return this.message;
  }
}
