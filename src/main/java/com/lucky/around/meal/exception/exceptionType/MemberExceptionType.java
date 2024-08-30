package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MemberExceptionType implements ExceptionType {
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 아이디의 회원이 없습니다.");

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
