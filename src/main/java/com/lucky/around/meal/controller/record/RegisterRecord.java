package com.lucky.around.meal.controller.record;

import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.entity.enums.MemberRole;

public record RegisterRecord(String memberName, String password, String email) {

  public Member toMember() {
    return Member.builder()
        .memberName(memberName())
        .password(password())
        .email(email())
        .lat(0)
        .lon(0)
        .role(MemberRole.USER)
        .build();
  }
}
