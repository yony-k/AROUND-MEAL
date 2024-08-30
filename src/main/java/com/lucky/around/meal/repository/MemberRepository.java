package com.lucky.around.meal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucky.around.meal.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByMemberName(String memberName);
}
