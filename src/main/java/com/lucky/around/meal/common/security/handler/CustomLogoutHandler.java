package com.lucky.around.meal.common.security.handler;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.lucky.around.meal.common.security.redis.RefreshToken;
import com.lucky.around.meal.common.security.redis.RefreshTokenRepository;
import com.lucky.around.meal.common.security.util.CookieProvider;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.SecurityExceptionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {

  private final CookieProvider cookieProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  // refreshToken 프리픽스
  @Value("${spring.data.redis.prefix}")
  String refreshTokenPrefix;

  public CustomLogoutHandler(
      CookieProvider cookieProvider, RefreshTokenRepository refreshTokenRepository) {
    this.cookieProvider = cookieProvider;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    try {
      // 리퀘스트에서 refreshToken 만료기한 0으로 설정
      Cookie findCookie = cookieProvider.deleteRefreshTokenCookie(request);
      // 리퀘스트에 쿠키 추가
      response.addCookie(findCookie);
      // redis에서 refreshToken 삭제
      deleteRefreshTokenInRedis(findCookie);
    } catch (Exception e) {
      log.error("로그아웃 오류: {}", e.getMessage());
      sendResponse(e.getMessage(), response);
    }
  }

  // redis에서 refreshToken 삭제하는 메소드
  private void deleteRefreshTokenInRedis(Cookie findCookie) {
    // redis에서 refreshToken 찾기
    Optional<RefreshToken> refreshToken =
        refreshTokenRepository.findById(refreshTokenPrefix + findCookie.getValue());
    if (refreshToken.isPresent()) {
      // redis에서 refreshToken 삭제
      refreshTokenRepository.delete(refreshToken.get());
    } else {
      throw new CustomException(SecurityExceptionType.REFRESHTOKEN_NOT_FOUND);
    }
  }

  // 클라이언트에게 응답
  private void sendResponse(String message, HttpServletResponse response) {
    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpStatus.NOT_FOUND.value());
    try {
      response.getWriter().write(message);
      response.getWriter().flush();
    } catch (IOException e) {
      log.error("응답 작성 중 오류 발생: {}", e.getMessage());
    }
  }
}
