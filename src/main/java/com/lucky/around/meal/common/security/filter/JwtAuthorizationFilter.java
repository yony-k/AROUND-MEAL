package com.lucky.around.meal.common.security.filter;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lucky.around.meal.common.security.util.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  // 필터 거치지 않는 경로
  @Value("${spring.excluded.path-list}")
  private List<String> excludedPaths;

  // 요청 주소가 필터를 거치지 않는 경로 목록에 존재할 시 필터 패스
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return excludedPaths.contains(path);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // request에서 accessToken 찾고 검증
    String accessToken = jwtProvider.validateToken(request);
    if (accessToken != null) {
      try {
        // accessToken을 사용해서 인증객체 등록
        SecurityContextHolder.getContext()
            .setAuthentication(jwtProvider.getAuthentication(accessToken));
        filterChain.doFilter(request, response);
      } catch (AuthenticationServiceException e) {
        throw e;
      }
    }
    filterChain.doFilter(request, response);
  }
}
