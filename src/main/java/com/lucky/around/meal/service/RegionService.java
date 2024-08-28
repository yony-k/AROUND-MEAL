package com.lucky.around.meal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lucky.around.meal.entity.Region;
import com.lucky.around.meal.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

  private final RegionRepository regionRepository;

  // 시군구 전체 조회
  public List<Region> getAllRegionList() {
    return regionRepository.findAll();
  }
}
