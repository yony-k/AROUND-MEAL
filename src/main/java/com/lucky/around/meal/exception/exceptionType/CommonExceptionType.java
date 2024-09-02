package com.lucky.around.meal.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonExceptionType implements ExceptionType {
  // 404 Not Found
  NOT_FOUND_PATH(HttpStatus.NOT_FOUND, "요청하신 경로를 찾을 수 없습니다"),

  // 400 Bad Request
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 값이 올바르지 않습니다"),
  INVALID_REQUEST_PARAM_TYPE(HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다"),
  NOT_NULL_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "필수 요청 파라미터 값을 입력해주세요"),

  // 415 UNSUPPORTED_MEDIA_TYPE
  INVALID_JSON_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "올바른 요청 형식이 아닙니다"),

  // 500 Internal Server Error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

  // 변환 에러
  TRANS_ENTITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Entity 변환 에러"),
  TRANS_JSON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 변환 에러"),

  // 평가 수 기준 업로드 시간
  INSPECTION_TIME(HttpStatus.NO_CONTENT, "목록을 다시 업로드 중입니다. 잠시 후 다시 시도해주세요.");

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
