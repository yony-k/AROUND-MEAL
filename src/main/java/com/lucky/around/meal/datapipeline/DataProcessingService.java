package com.lucky.around.meal.datapipeline;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProcessingService {

  private final RawRestaurantRepository rawRestaurantRepository;
  private final RestaurantRepository restaurantRepository;
  private final ObjectMapper objectMapper;

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  //  @Scheduled(fixedRate = 900_000)
  public void processRawData() {
    try {
      int page = 0;

      while (true) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        Page<RawRestaurant> rawRestaurantsPage = rawRestaurantRepository.findAll(pageRequest);

        if (rawRestaurantsPage.isEmpty()) {
          break; // 더 이상 처리할 데이터가 없으면 종료
        }

        List<RawRestaurant> rawRestaurants = rawRestaurantsPage.getContent();
        List<Restaurant> processedRestaurants =
            rawRestaurants.stream()
                .map(this::convertToProcessedRestaurant)
                .collect(Collectors.toList());

        restaurantRepository.saveAll(processedRestaurants);

        page++;
      }
    } catch (Exception e) {
      log.error("Error during data processing", e);
    }
  }

  private Restaurant convertToProcessedRestaurant(RawRestaurant rawRestaurant) {
    try {
      JsonNode rootNode = objectMapper.readTree(rawRestaurant.getJsonData());

      String id = rawRestaurant.getId();
      String restaurantName = rootNode.path("BPLCNM").asText();
      String category = rootNode.path("UPTAENM").asText();
      String restaurantTel = rootNode.path("SITETEL").asText();

      String jibunAddress = rootNode.path("SITEWHLADDR").asText();
      String[] jibunAddresses = splitAddress(jibunAddress);
      String dosi = jibunAddresses[0];
      String sigungu = jibunAddresses[1];
      String jibunDetailAddress = jibunAddresses[2];

      String doroAddress = rootNode.path("RDNWHLADDR").asText();
      String[] doroAddresses = splitAddress(doroAddress);
      String doroDetailAddress = doroAddresses[2];

      Double lon = parseDouble(rootNode.path("X").asText());
      Double lat = parseDouble(rootNode.path("Y").asText());

      return Restaurant.builder()
          .id(id)
          .restaurantName(restaurantName)
          .category(Category.of(category))
          .restaurantTel(restaurantTel)
          .jibunDetailAddress(jibunDetailAddress)
          .doroDetailAddress(doroDetailAddress)
          .dosi(dosi)
          .sigungu(sigungu)
          .lon(lon)
          .lat(lat)
          .build();
    } catch (Exception e) {
      log.error("Error converting RawRestaurant to ProcessedRestaurant", e);
      return null; // 혹은 예외 처리 로직을 추가
    }
  }

  private String[] splitAddress(String address) {
    if (address == null || address.isEmpty()) {
      return new String[] {"", "", ""};
    }
    return address.split(" ", 3);
  }

  private Double parseDouble(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }
}
