package com.lucky.around.meal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating {
  /*
      유저(member) fk 평가를 생성한 유저 FK
      맛집(restaurant) fk 유저가 평가한 대상 맛집 FK
      점수(score) integer 0 ~ 5 에 해당하는 점수 [DTO,SERVICE]
      내용(content) string 평가 내용이며 0~255자 이내 작성 [DTO,SERVICE]
  */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  private Integer score;
  private String content;
  private LocalDateTime createAt;

  @Builder
  public Rating(
      Member member, Restaurant restaurant, Integer score, String content, LocalDateTime createAt) {
    this.member = member;
    this.restaurant = restaurant;
    this.score = score;
    this.content = content;
    this.createAt = createAt;
  }

  @PrePersist
  public void prePersist() {
    if (this.createAt == null) {
      this.createAt = LocalDateTime.now();
    }
  }
}
