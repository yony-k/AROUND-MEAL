package com.lucky.around.meal.service;

import org.springframework.data.geo.Point;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucky.around.meal.common.redis.RedisRepository;
import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.controller.request.*;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.*;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.MemberExceptionType;
import com.lucky.around.meal.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

  private final RedisRepository redis;
  private final MemberRepository memberRepository;

  // Redis에 실시간 위치 정보 저장
  public void saveMemberLocation(MemberLocationRequestDto request) {
    String key = generateRedisKey(getCurrentMemberId());
    redis.saveRealTimeLocation(key, request.lat(), request.lon(), getCurrentMemberId());
  }

  // Redis에서 실시간 위치 정보 조회
  public Point getMemberLocation() {
    String key = generateRedisKey(getCurrentMemberId());
    return redis.getRealTimeLocation(key, getCurrentMemberId());
  }

  // 회원의 정적인 위치 정보 업데이트
  @Transactional
  public void updateStaticLocation(StaticLocationRequestDto request) {
    Member member = findMemberById(getCurrentMemberId());
    member.updateLocation(request.lon(), request.lat());
  }

  // 회원의 정적인 위치 정보 조회
  public StaticLocationResponseDto getStaticLocation() {
    Member member = findMemberById(getCurrentMemberId());
    return new StaticLocationResponseDto(member.getLat(), member.getLon());
  }

  // 회원 실시간 위치 정보 DTO 변환
  public LocationResponseDto getMemberLocationToTrans() {
    Point redisPoint = getMemberLocation();
    return new LocationResponseDto(redisPoint.getY(), redisPoint.getX());
  }

  private String generateRedisKey(Long memberId) {
    return "location:" + memberId;
  }

  private Member findMemberById(Long memberId) {
    return memberRepository
        .findById(memberId)
        .orElseThrow(() -> new CustomException(MemberExceptionType.MEMBER_NOT_FOUND));
  }

  private Long getCurrentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
    return principal.getMemberId();
  }
}
