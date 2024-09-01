package com.lucky.around.meal.datapipeline;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProcessService {

  private final RawRestaurantRepository rawRestaurantRepository;
  private final RestaurantRepository restaurantRepository;
  private final ObjectMapper objectMapper;

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  public synchronized void executeDataProcess() {
    log.info("[executeDataProcess] 데이터 가공하기 실행");
    //        throw new RuntimeException("데이터 가공하기에서 일부러 발생시킨 예외입니다.");
  }
}
