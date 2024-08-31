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

  // 엑세스 토큰, 리프레시 토큰 재발급
  public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

    // 리퀘스트에서 refreshToken 빼오기
    Optional<Cookie> findCookie = getCookie(request);

    // redis에서 쿠키 값을 이용해 refreshToken 가져오기
    Optional<RefreshToken> refreshToken = getRefreshToken(findCookie);

    // DB에서 Member 객체 가져오기
    Member findMember = getMember(refreshToken.get().getMemberId());

    // 토큰 재발급 후 헤더, 쿠키, redis에 토큰 저장
    reissueToken(findMember, response);
  }

  // 리퀘스트에서 refreshToken 빼오기
  private Optional<Cookie> getCookie(HttpServletRequest request) {
    Optional<Cookie> findCookie = cookieProvider.getRefreshTokenCookie(request);
    if (!findCookie.isPresent()) throw new CustomException(SecurityExceptionType.COOKIE_NOT_FOUND);
    return findCookie;
  }

  // redis에서 쿠키 값을 이용해 refreshToken 가져오기
  private Optional<RefreshToken> getRefreshToken(Optional<Cookie> findCookie) {
    Optional<RefreshToken> refreshToken =
        refreshTokenRepository.findById(refreshTokenPrefix + findCookie.get().getValue());
    if (!refreshToken.isPresent())
      throw new CustomException(SecurityExceptionType.REFRESHTOKEN_NOT_FOUND);
    return refreshToken;
  }

  // Member 객체 가져오기
  private Member getMember(String memberId) {
    // 계정 아이디 찾아오기
    long savedMemberId = Long.parseLong(memberId);

    // memberRepository 에서 사용자 정보 가져오기
    Member findMember =
        memberRepository
            .findById(savedMemberId)
            .orElseThrow(() -> new CustomException(MemberExceptionType.MEMBER_NOT_FOUND));
    return findMember;
  }

  // 토큰 재발급
  private void reissueToken(Member findMember, HttpServletResponse response) {
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
            refreshTokenPrefix + reissueToken.refreshToken(),
            String.valueOf(findMember.getMemberId())));
  }
}
