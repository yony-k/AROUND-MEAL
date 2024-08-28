package com.lucky.around.meal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.entity.Region;
import com.lucky.around.meal.service.RegionService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/region")
@RestController
@RequiredArgsConstructor
public class RegionController {

  private final RegionService regionService;

  // 시군구 전체 목록 조회
  @GetMapping("/allRegionList")
  public ResponseEntity<List<Region>> getAllRegionList() {
    return ResponseEntity.ok(regionService.getAllRegionList());
  }
}
