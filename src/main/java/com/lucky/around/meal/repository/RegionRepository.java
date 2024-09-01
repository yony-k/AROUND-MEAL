package com.lucky.around.meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucky.around.meal.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

  // 데이터 중복 방지 코드 추가
  boolean existsByDosiAndSigungu(String dosi, String sigungu);
}
