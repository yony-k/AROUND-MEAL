package com.lucky.around.meal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.controller.record.RegionRecord;
import com.lucky.around.meal.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

  private final RegionRepository regionRepository;

  // 시군구 전체 조회
  // value = "regionList" [캐시], key = redis의 key 값
  // ex) regionList::allRegions
  @Cacheable(value = "regionList", key = "'allRegions'")
  public List<RegionRecord> getAllRegionList() {
    log.info("Fetching all regions from the database");
    return regionRepository.findAll().stream()
        .map(RegionRecord::fromEntity)
        .collect(Collectors.toList());
  }
}
