package com.lucky.around.meal.datapipeline;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveService {

  private final DataQueue dataQueue;
  private final RestaurantRepository restaurantRepository;

  @Async
  public void saveData() {
    try {
      log.info("저장 : " + dataQueue.getProcessQueue().size() + "의 데이터 저장 예정");

      while (true) {
        // 가공된 데이터 가져오기
        ParsedData parsedData = dataQueue.getProcessQueue().take();

        // 데이터 저장하기
        Restaurant restaurant = parsedData.getData();
        restaurantRepository.save(restaurant);
        //        log.info("저장 : " + restaurant.getId());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
