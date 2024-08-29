package com.lucky.around.meal.common.security.handler;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.lucky.around.meal.common.security.redis.RefreshToken;
import com.lucky.around.meal.common.security.redis.RefreshTokenRepository;
import com.lucky.around.meal.common.security.util.CookieProvider;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.SecurityExceptionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

  private final CookieProvider cookieProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver;

  public CustomLogoutSuccessHandler(
      CookieProvider cookieProvider,
      RefreshTokenRepository refreshTokenRepository,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.cookieProvider = cookieProvider;
    this.refreshTokenRepository = refreshTokenRepository;
    this.resolver = resolver;
  }

  // refreshToken 프리픽스
  @Value("${spring.data.redis.prefix}")
  String refreshTokenPrefix;

  @Override
  public void onLogoutSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    // 로그인 하지 않은 사용자가 로그아웃 신청 했을 시 처리
    if (authentication == null) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("text/plain; charset=UTF-8");
      response.getWriter().write("로그인 해주세요.");
      response.getWriter().flush();
      return;
    }

    try {
      // 리퀘스트에서 refreshToken 만료기한 0으로 설정
      Cookie findCookie = cookieProvider.deleteRefreshTokenCookie(request);
      // 리퀘스트에 쿠키 추가
      response.addCookie(findCookie);
      // redis에서 refreshToken 삭제
      deleteRefreshTokenInRedis(findCookie);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      resolver.resolveException(request, response, null, e);
    }
  }

  // redis에서 refreshToken 삭제하는 메소드
  public void deleteRefreshTokenInRedis(Cookie findCookie) {
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
}
