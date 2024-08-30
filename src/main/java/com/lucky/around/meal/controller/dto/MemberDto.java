package com.lucky.around.meal.controller.dto;

import com.lucky.around.meal.entity.Member;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class MemberDto {
  private String memberName;
  private String email;
  private double lat;
  private double lon;

  public MemberDto(Member member) {
    this.memberName = member.getMemberName();
    this.email = member.getEmail();
    this.lat = member.getLat();
    this.lon = member.getLon();
  }
}
