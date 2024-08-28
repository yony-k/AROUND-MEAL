package com.lucky.around.meal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucky.around.meal.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
  Optional<Region> findByDosiAndSigungu(String dosi, String sigungu);
}
