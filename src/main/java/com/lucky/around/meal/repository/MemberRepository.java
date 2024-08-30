package com.lucky.around.meal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucky.around.meal.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  // 로그인 시 memberName으로 사용자 반환
  Optional<Member> findByMemberName(String memberName);

  // memberName 중복 검증시 사용
  boolean existsByMemberName(String memberName);
}
