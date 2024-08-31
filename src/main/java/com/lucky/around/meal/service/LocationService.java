package com.lucky.around.meal.service;

import org.springframework.data.geo.Point;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.common.redis.RedisRepository;
import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.controller.request.*;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.*;
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

  // 회원 실시간 위치 정보 DTO 변환
  public LocationResponseDto getMemberLocationToTrans() {
    Point redisPoint = getMemberLocation();
    return new LocationResponseDto(redisPoint.getY(), redisPoint.getX());
  }

  private String generateRedisKey(Long memberId) {
    return "location:" + memberId;
  }

  private Long getCurrentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
    return principal.getMemberId();
  }
}
