package com.lucky.around.meal.common.discord.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lucky.around.meal.common.discord.dto.DiscordMessageDto;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.CommonExceptionType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class DiscordService {
  public void sendMsg(
      final String memberName, final Restaurant restaurant, final String memberWebhookUrl) {
    try {
      String msg = getMessage(memberName, restaurant);
      WebClient webClient =
          WebClient.builder()
              .baseUrl(memberWebhookUrl)
              .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .build();

      WebClient.ResponseSpec responseSpec =
          webClient.post().bodyValue(DiscordMessageDto.of(msg)).retrieve();

      responseSpec.toBodilessEntity().block();

    } catch (RuntimeException e) {
      log.error("에러 발생 :: " + e);
      throw new CustomException(CommonExceptionType.DISCORD_SEND_ERROR);
    }
  }

  private String getMessage(String memberName, Restaurant restaurant) {
    return memberName
        + "님, 오늘의 추천 식당은 "
        + restaurant.getRestaurantName()
        + "입니다."
        + "\n"
        + "식당 위치: "
        + restaurant.getDoroAddress();
  }
}
