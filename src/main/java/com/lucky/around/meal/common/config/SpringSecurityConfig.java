package com.lucky.around.meal.common.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.lucky.around.meal.common.security.filter.JwtAuthenticationFilter;
import com.lucky.around.meal.common.security.filter.JwtAuthorizationFilter;
import com.lucky.around.meal.common.security.handler.*;
import com.lucky.around.meal.common.security.redis.RefreshTokenRepository;
import com.lucky.around.meal.common.security.util.CookieProvider;
import com.lucky.around.meal.common.security.util.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
  // jwt 관리 클래스
  private final JwtProvider jwtProvider;
  // cookie 관리 클래스
  private final CookieProvider cookieProvider;
  // refreshToken 리포지토리
  private final RefreshTokenRepository refreshTokenRepository;
  // 인증 관리자 생성을 위한 설정파일
  private final AuthenticationConfiguration authenticationConfiguration;
  // 예외 처리 핸들러 설정
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomLogoutHandler customLogoutHandler;
  private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

  // accessToken 헤더 이름
  @Value("${jwt.access-token-header}")
  public String accessTokenHeader;

  // 비밀번호 암호화
  @Bean
  public static BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 인증 관리자-로그인 시 이 클래스를 통해 인증을 한다.
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  // 특정 요청 경로에 대해서는 시큐리티 검사 무시
  // resources는 이미지, css, javascript 파일등에 대한 요청을 의미함
  @Bean
  WebSecurityCustomizer webSecurityCustomizer() {
    return (webSecurity) -> webSecurity.ignoring().requestMatchers("/resources/**");
  }

  // 인증 관련 필터
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
    JwtAuthenticationFilter filter =
        new JwtAuthenticationFilter(jwtProvider, cookieProvider, refreshTokenRepository);
    filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
    return filter;
  }

  // 인가 관련 필터
  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
    return new JwtAuthorizationFilter(jwtProvider);
  }

  // 지정된 출처(주소)에서 오는 요청 관련 설정
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    return request -> {
      CorsConfiguration config = new CorsConfiguration();
      // 서버에서 내려보내는 헤더의 특정 내용을 볼 수 있도록 허용
      config.addExposedHeader(accessTokenHeader);
      // 클라이언트에서 보내오는 헤더와 메서드 허용
      config.setAllowedHeaders(Collections.singletonList("*"));
      config.setAllowedMethods(Collections.singletonList("*"));
      // 특정 출처에서 오는 요청만 허용
      config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:8080"));
      // 쿠키 등 자격증명이 포함된 요청 허용
      config.setAllowCredentials(true);
      return config;
    };
  }

  // 필터체인 설정
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // 사용자가 의도하지 않은 요청 방지 설정
        .csrf(csrfConf -> csrfConf.disable())
        // 위에서 적은 Cors 설정 적용
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        // 기본 폼 로그인 기능 비활성화
        .formLogin(loginConf -> loginConf.disable())
        // 특정 경로에 대한 인가 설정
        .authorizeHttpRequests(
            authz ->
                authz
                    // 관리자
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    // 회원가입, 사용자 정보 요청
                    .requestMatchers(HttpMethod.GET, "/api/members")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/members")
                    .permitAll()
                    // 로그인, 액세스 토큰 재발급
                    .requestMatchers("/api/members/login", "/api/members/refresh_token")
                    .permitAll()
                    // 이외 모든 요청 인증 필요
                    .anyRequest()
                    .authenticated())
        // 예외 처리 핸들러 설정
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler))
        // 로그아웃 처리
        .logout(
            logout ->
                logout
                    .addLogoutHandler(customLogoutHandler)
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .logoutUrl("/api/members/logout"));
    // 커스텀 필터 설정
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);

    return http.build();
  }
}
