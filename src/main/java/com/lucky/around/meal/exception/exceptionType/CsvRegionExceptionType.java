package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CsvRegionExceptionType implements ExceptionType {
  FILE_NOT_FOUNDED(HttpStatus.NOT_FOUND, "CSV 파일을 찾을 수 없습니다"),
  FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CSV 파일 읽기 중 오류가 발생했습니다.");

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
