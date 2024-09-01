package com.lucky.around.meal.datapipeline;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
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
  private final GeometryUtil geometryUtil;

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  public synchronized void executeDataProcess() {
    log.info("[executeDataProcess] 데이터 가공하기 실행");

    try {
      int page = 0;

      while (true) {
        Pageable pageRequest = PageRequest.of(page, PAGE_SIZE);
        Page<RawRestaurant> rawRestaurantsPage = rawRestaurantRepository.findAll(pageRequest);

        if (rawRestaurantsPage.isEmpty()) {
          break; // 더 이상 처리할 데이터가 없으면 종료
        }

        List<RawRestaurant> rawRestaurants = rawRestaurantsPage.getContent();

        for (RawRestaurant rawRestaurant : rawRestaurants) {
          if (rawRestaurant.isUpdated()) {
            log.info("[dataProcessing] 변경된 맛집 : {}", rawRestaurant.getJsonData());

            try {
              Restaurant processedRestaurant = convertToProcessedRestaurant(rawRestaurant);
              log.info("processedRestaurant: " + processedRestaurant);
              if (processedRestaurant != null) {
                restaurantRepository.save(processedRestaurant);
                rawRestaurant.setUpdated(false);
                rawRestaurantRepository.save(rawRestaurant);
              }
            } catch (Exception e) {
              log.error("[dataProcessing] failed restaurant : {}", rawRestaurant.getId(), e);
            }
          }
        }
        page++;
      }
    } catch (Exception e) {
      log.error("[processRawData] error", e);
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

      String xStr = rootNode.path("X").asText();
      String yStr = rootNode.path("Y").asText();

      Double longitude = xStr.isEmpty() ? null : Double.parseDouble(xStr);
      Double latitude = yStr.isEmpty() ? null : Double.parseDouble(yStr);

      Point location =
          (longitude != null && latitude != null)
              ? geometryUtil.createPoint(longitude, latitude)
              : null;

      return Restaurant.builder()
          .id(id)
          .restaurantName(restaurantName)
          .category(Category.of(category))
          .restaurantTel(restaurantTel)
          .jibunDetailAddress(jibunDetailAddress)
          .doroDetailAddress(doroDetailAddress)
          .dosi(dosi)
          .sigungu(sigungu)
          .location(location)
          .build();
    } catch (Exception e) {
      log.error("[convertToProcessedRestaurant] error ", e);
      return null;
    }
  }

  private String[] splitAddress(String address) {
    if (address == null || address.isEmpty()) {
      return new String[] {"", "", ""};
    }
    return address.split(" ", 3);
  }
}
