package com.lucky.around.meal.common.security.handler;

import java.io.IOException;
import java.security.SignatureException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.lucky.around.meal.exception.exceptionType.SecurityExceptionType;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

// 로그인 후 이용가능한 요청 했을 때 사용되는 핸들러
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    // 예외 종류 확인
    Exception e = (Exception) request.getAttribute("exception");
    String message = "";

    if (e == null) {
      message = SecurityExceptionType.REQUIRED_AUTHENTICATION.getMessage();
    } else {
      message = getExceptionMessage(e);
    }

    // 클라이언트에게 응답
    sendResponse(message, response);
  }

  // 예외 종류 확인해서 적절한 메세지 리턴
  private String getExceptionMessage(Exception e) {
    String message = "";
    if (e instanceof MalformedJwtException) {
      message = SecurityExceptionType.INVALID_JWT_TOKEN.getMessage();
    } else if (e instanceof ExpiredJwtException) {
      message = SecurityExceptionType.EXPIRED_JWT_TOKEN.getMessage();
    } else if (e instanceof SignatureException) {
      message = SecurityExceptionType.INVALID_JWT_SIGNATURE.getMessage();
    } else if (e instanceof UnsupportedJwtException) {
      message = SecurityExceptionType.UNSUPPORTED_JWT_TOKEN.getMessage();
    } else if (e instanceof IllegalArgumentException) {
      message = SecurityExceptionType.EMPTY_JWT_CLAIMS.getMessage();
    } else if (e instanceof JwtException) {
      message = SecurityExceptionType.JWT_PROCESSING_ERROR.getMessage();
    } else if (e instanceof SecurityException) {
      message = SecurityExceptionType.GENERAL_SECURITY_ERROR.getMessage();
    } else {
      message = SecurityExceptionType.SERVER_ERROR.getMessage();
    }
    return message;
  }

  // 클라이언트에게 응답 보내기
  private void sendResponse(String message, HttpServletResponse response) throws IOException {
    log.error("검증 실패 : {}", message);
    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getWriter().write(message);
    response.getWriter().flush();
  }
}
