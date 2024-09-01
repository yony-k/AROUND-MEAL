package com.lucky.around.meal.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.repository.RestaurantRepository;

@SpringBootTest
public class RestaurantRepositoryTest {

  @Autowired private RestaurantRepository restaurantRepository;

  @Test
  @DisplayName("평가 수 기준 맛집 목록 가져오기 성공 테스트")
  public void findRestaurantByRatingCountSuccessTest() {
    // given

    // when
    List<Restaurant> restaurantList = restaurantRepository.findRestaurantByRatingCount(1);

    // then
    assertNotNull(restaurantList);
    assertEquals(1, restaurantList.size());
  }

  @Test
  @DisplayName("평가 수 기준 맛집 목록 가져오기 실패 테스트")
  public void findRestaurantByRatingCountFailureTest() {
    // given

    // when
    List<Restaurant> restaurantList = restaurantRepository.findRestaurantByRatingCount(2);

    // then
    assertEquals(0, restaurantList.size());
  }
}
