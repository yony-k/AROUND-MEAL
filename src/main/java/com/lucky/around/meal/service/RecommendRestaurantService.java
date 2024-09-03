package com.lucky.around.meal.service;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.common.discord.service.DiscordService;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.repository.MemberRepository;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendRestaurantService {
  private final DiscordService discordMessageService;
  private final MemberRepository memberRepository;
  private final RestaurantRepository restaurantRepository;
  private final GeometryUtil geometryUtil;

  // @Scheduled(cron = "0 12 * * *")
  @Scheduled(cron = "0 */2 * * * *")
  public void recommendRestaurant() {
    List<Member> members = memberRepository.findAllWithLunchRecommendAgree();
    for (Member member : members) {
      // TODO: 사람의 위치를 확인하고 포함되는 위치에 대한 지역별 맛집을 미리 선정해두고 알림을 보내도록 수정
      // TODO: 지역별 맛집을 미리 계산하는 스케줄링 추가 필요
      Point memberLocation = geometryUtil.createPoint(member.getLon(), member.getLat());
      Restaurant recommendedRestaurant =
          restaurantRepository.findRecommendedRestaurantForMember(memberLocation).orElse(null);
      discordMessageService.sendMsg(
          member.getMemberName(), recommendedRestaurant, member.getMemberWebhookUrl());
    }
  }
}
