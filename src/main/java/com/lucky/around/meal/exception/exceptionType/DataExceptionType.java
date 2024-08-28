package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DataExceptionType implements ExceptionType {
  DATA_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 전처리에 실패했습니다."),
  SCHEDULING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄러 실행을 실패했습니다.");

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
