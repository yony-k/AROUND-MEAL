package com.lucky.around.meal.service;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.scheduling.annotation.Async;
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

  @Scheduled(cron = "0 0 12 * * *") // 매일 12시에 실행
  // @Scheduled(cron = "0 */2 * * * *") : 로컬 테스트 용
  public void recommendRestaurant() {
    List<Member> members = memberRepository.findAllWithLunchRecommendAgree();
    for (Member member : members) {
      sendAsyncRestaurantRecommendation(member);
    }
  }

  @Async
  public void sendAsyncRestaurantRecommendation(Member member) {
    Point memberLocation = geometryUtil.createPoint(member.getLon(), member.getLat());
    Restaurant recommendedRestaurant =
        restaurantRepository.findRecommendedRestaurantForMember(memberLocation).orElse(null);
    discordMessageService.sendMsg(
        member.getMemberName(), recommendedRestaurant, member.getMemberWebhookUrl());
  }
}
