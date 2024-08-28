package com.lucky.around.meal.entity;

import jakarta.persistence.*;

import com.lucky.around.meal.entity.enums.MemberRole;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private MemberRole role;
}
