package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MemberExceptionType implements ExceptionType {
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 회원 정보를 찾을 수 없습니다."),
  REALTIME_LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "회원의 실시간 위치 정보를 찾을 수 없습니다."),
  LOCATION_NOT_FOUNT(HttpStatus.NOT_FOUND, "회원의 위치 정보를 찾을 수 없습니다."),
  ;

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
