package com.lucky.around.meal.exception;

import com.lucky.around.meal.exception.exceptionType.ExceptionType;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
  private final ExceptionType exceptionType;

  public CustomException(ExceptionType exceptionType) {
    super(exceptionType.message());
    this.exceptionType = exceptionType;
  }
}
