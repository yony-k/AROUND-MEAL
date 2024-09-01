package com.lucky.around.meal.service;

import java.util.List;
import java.util.stream.Collectors;

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
  public List<RegionRecord> getAllRegionList() {
    return regionRepository.findAll().stream()
        .map(RegionRecord::fromEntity)
        .collect(Collectors.toList());
  }
}
