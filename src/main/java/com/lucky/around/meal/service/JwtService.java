package com.lucky.around.meal.service;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.common.security.record.JwtRecord;
import com.lucky.around.meal.common.security.redis.RefreshToken;
import com.lucky.around.meal.common.security.redis.RefreshTokenRepository;
import com.lucky.around.meal.common.security.util.CookieProvider;
import com.lucky.around.meal.common.security.util.JwtProvider;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.MemberExceptionType;
import com.lucky.around.meal.exception.exceptionType.SecurityExceptionType;
import com.lucky.around.meal.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
  // jwt 관리 클래스
  private final JwtProvider jwtProvider;
  // 쿠키 관리 클래스
  private final CookieProvider cookieProvider;
  // redis 리포지토리
  private final RefreshTokenRepository refreshTokenRepository;
  // Member 리포지토리
  private final MemberRepository memberRepository;

  // refreshToken 프리픽스
  @Value("${spring.data.redis.prefix}")
  String refreshTokenPrefix;

  public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

    // 리퀘스트에서 refreshToken 빼오기
    Optional<Cookie> findCookie = cookieProvider.getRefreshTokenCookie(request);
    if (!findCookie.isPresent()) throw new CustomException(SecurityExceptionType.COOKIE_NOT_FOUND);

    // redis에서 쿠키 값을 이용해 refreshToken 가져오기
    Optional<RefreshToken> refreshToken =
        refreshTokenRepository.findById(refreshTokenPrefix + findCookie.get().getValue());
    if (!refreshToken.isPresent())
      throw new CustomException(SecurityExceptionType.REFRESHTOKEN_NOT_FOUND);

    // 계정 아이디 찾아오기
    long memberId = Long.parseLong(refreshToken.get().getMemberId());

    // memberRepository 에서 사용자 정보 가져오기
    Member findMember =
        memberRepository
            .findById(memberId)
            .orElseThrow(() -> new CustomException(MemberExceptionType.NOT_FOUND_MEMBER));

    // 사용자 정보로 리프레시 토큰, 엑세스 토큰 다시 만들기
    JwtRecord reissueToken = jwtProvider.getReissueToken(findMember);

    // accessToken은 헤더에 저장
    response.setHeader(
        jwtProvider.accessTokenHeader, jwtProvider.prefix + reissueToken.accessToken());
    // refreshToken은 쿠키에 저장
    response.addCookie(cookieProvider.createRefreshTokenCookie(reissueToken.refreshToken()));
    // redis에 refreshToken 저장, memberId는 String으로 변환 후 저장
    refreshTokenRepository.save(
        new RefreshToken(
            refreshTokenPrefix + reissueToken.refreshToken(), String.valueOf(memberId)));
  }
}
