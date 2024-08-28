package com.lucky.around.meal.controller.data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.service.data.DataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DataController {

  private final DataService dataService;

  // 테스트용 메소드
  @GetMapping("/seoul-restaurant-data")
  public String getSeoulRestaurantData() {
    return dataService.getResult();
  }

  // 테스트용 메소드
  @PostMapping("/restaurant")
  public void saveRestaurant() {
    dataService.saveRestaurant();
  }
}
