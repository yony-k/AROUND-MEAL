package com.lucky.around.meal.common.discord.dto;

public record DiscordMessageDto(String content) {
  public static DiscordMessageDto of(String content) {
    return new DiscordMessageDto(content);
  }
}
