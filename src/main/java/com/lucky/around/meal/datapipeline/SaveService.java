package com.lucky.around.meal.datapipeline;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveService {

  private final DataQueue dataQueue;
  private final RestaurantJdbcRepository restaurantJdbcRepository;

  @Async
  public void saveData() {
    try {
      while (true) {
        // 가공된 데이터 가져오기
        ParsedData parsedData = dataQueue.getProcessQueue().take();
        restaurantJdbcRepository.saveAll(parsedData.getData());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
