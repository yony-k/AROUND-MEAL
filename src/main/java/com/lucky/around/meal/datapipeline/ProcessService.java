package com.lucky.around.meal.datapipeline;

import java.util.concurrent.BlockingDeque;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.common.util.GeometryUtil;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {

  private final DataQueue dataQueue;
  private final ObjectMapper objectMapper;
  private final GeometryUtil geometryUtil;

  @Value("${API_SERVICE_NAME}")
  private String SERVICE_NAME;

  @Async
  public void processData() {
    try {
      // while이면 언제 반복이 끝나는거지? 큐가 비어있을 때?
      while (true) {
        // 수집한 데이터 가져오기
        RawData rawData = dataQueue.getCollectQueue().take();
        log.info("가공: " + rawData.getData());

        // 데이터 가공하기
        JsonNode rootNode = objectMapper.readTree(rawData.getData());
        JsonNode rowNodes = rootNode.path(SERVICE_NAME).path("row");

        for (JsonNode rowNode : rowNodes) {
          String id = rowNode.path("MGTNO").asText("");

          // 좌표가 유효하지 않은 음식점은 저장하지 않기
          String xStr = rowNode.path("X").asText();
          String yStr = rowNode.path("Y").asText();

          if (xStr.isEmpty() || yStr.isEmpty()) continue;

          double longitude = Double.parseDouble(xStr);
          double latitude = Double.parseDouble(yStr);
          Point location = geometryUtil.createPoint(longitude, latitude);

          String restaurantName = rowNode.path("BPLCNM").asText();
          String category = rowNode.path("UPTAENM").asText();
          String tel = rowNode.path("SITETEL").asText().replace(" ", "");

          String jibunAddress = rowNode.path("SITEWHLADDR").asText();
          String[] jibunAddresses = splitAddress(jibunAddress);
          String dosi = jibunAddresses[0];
          String sigungu = jibunAddresses[1];
          String jibunDetailAddress = jibunAddresses[2];

          String doroAddress = rowNode.path("RDNWHLADDR").asText();
          String[] doroAddresses = splitAddress(doroAddress);
          String doroDetailAddress = doroAddresses[2];

          Restaurant restaurant =
              Restaurant.builder()
                  .id(id)
                  .restaurantName(restaurantName)
                  .category(Category.of(category))
                  .restaurantTel(tel)
                  .jibunDetailAddress(jibunDetailAddress)
                  .doroDetailAddress(doroDetailAddress)
                  .dosi(dosi)
                  .sigungu(sigungu)
                  .location(location)
                  .build();

          //          log.info("id: " + restaurant.getId() + ", restaurantName: " +
          // restaurant.getRestaurantName());

          // 가공된 데이터 큐에 넣기
          BlockingDeque<ParsedData> processQueue = dataQueue.getProcessQueue();
          ParsedData parsedData = new ParsedData(restaurant);
          processQueue.put(parsedData);
          log.info("가공 큐 크기: " + processQueue.size());
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private String[] splitAddress(String address) {
    if (address == null || address.isEmpty()) {
      return new String[] {"", "", ""};
    }
    return address.split(" ", 3);
  }
}
