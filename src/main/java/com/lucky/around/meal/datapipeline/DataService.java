package com.lucky.around.meal.datapipeline;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.DataExceptionType;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

  private final WebClient.Builder webClientBuilder;
  private final RestaurantRepository restaurantRepository;
  private final ObjectMapper objectMapper;

  @Value("${API_BASE_URL}")
  private String BASE_URL;

  @Value("${API_KEY}")
  private String KEY;

  @Value("${API_SERVICE_NAME}")
  private String SERVICE_NAME;

  @Value("${API_FORMAT_TYPE}")
  private String FORMAT_TYPE;

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  // 데이터 수집
  public Mono<String> fetchData(int startIndex, int endIndex) {
    String uri =
        String.format("/%s/%s/%s/%s/%s", KEY, FORMAT_TYPE, SERVICE_NAME, startIndex, endIndex);

    WebClient webClient = webClientBuilder.baseUrl(BASE_URL).build();

    return webClient.get().uri(uri).retrieve().bodyToMono(String.class);
  }

  // 데이터 파싱 및 엔티티 변환
  private List<Restaurant> parseAndConvertToEntity(String data) {
    try {
      JsonNode rootNode = objectMapper.readTree(data);
      JsonNode rowNodes = rootNode.path(SERVICE_NAME).path("row");

      return StreamSupport.stream(rowNodes.spliterator(), false)
          .map(
              rowNode -> {
                String id = getOrEmpty(rowNode, "MGTNO");
                String restaurantName = getOrEmpty(rowNode, "BPLCNM");
                String category = getOrEmpty(rowNode, "UPTAENM");
                String restaurantTel = getOrEmpty(rowNode, "SITETEL");
                Double lon = validateCoordinate(getOrEmpty(rowNode, "X"));
                Double lat = validateCoordinate(getOrEmpty(rowNode, "Y"));

                String jibunAddress = getOrEmpty(rowNode, "SITEWHLADDR");
                String[] jibunAddresses = splitAddress(jibunAddress);
                String dosi = jibunAddresses[0];
                String sigungu = jibunAddresses[1];
                String jibunDetailAddress = jibunAddresses[2];

                String doroAddress = getOrEmpty(rowNode, "RDNWHLADDR");
                String[] doroAddresses = splitAddress(doroAddress);
                String doroDetailAddress = doroAddresses[2];

                return Restaurant.builder()
                    .id(id)
                    .restaurantName(restaurantName)
                    .restaurantTel(restaurantTel)
                    .jibunDetailAddress(jibunDetailAddress)
                    .doroDetailAddress(doroDetailAddress)
                    .category(Category.of(category))
                    .dosi(dosi)
                    .sigungu(sigungu)
                    .lon(lon)
                    .lat(lat)
                    .build();
              })
          .collect(Collectors.toList());

    } catch (Exception e) {
      log.info("[parseAndConvertData] error - " + e);
      throw new CustomException(DataExceptionType.DATA_PARSING_FAILED);
    }
  }

  // 데이터가 있으면 그대로 반환, 없으면 "" 반환
  private String getOrEmpty(JsonNode node, String field) {
    return node.path(field).asText("");
  }

  // 주소 분할
  private String[] splitAddress(String address) {
    if (address == null || address.isEmpty()) {
      return new String[] {"", "", ""};
    }
    return address.split(" ", 3);
  }

  // "" 값 일 때, 0.0 넣어주기
  private Double validateCoordinate(String coordinate) {
    if (coordinate == null || coordinate.trim().isEmpty()) {
      return 0.0;
    }
    return Double.parseDouble(coordinate);
  }

  // 데이터 저장
  public void saveData(List<Restaurant> restaurants) {
    try {
      restaurantRepository.saveAll(restaurants);
    } catch (Exception e) {
      log.error("[saveData] error - " + e);
    }
  }

  // 스케줄러 설정
  //  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시 실행
  //  @Scheduled(fixedRate = 900_000) // 테스트용 10분 마다 실행
  public void executeDataPipeline() {
    try {
      log.info("[executeDataPipeline] started!");

      int startIndex = 1;
      boolean hasMoreData = true;

      while (hasMoreData) {
        int endIndex = startIndex + PAGE_SIZE - 1;
        String responseData = fetchData(startIndex, endIndex).block();
        List<Restaurant> restaurants = parseAndConvertToEntity(responseData);

        if (restaurants.isEmpty()) {
          hasMoreData = false;
        } else {
          saveData(restaurants);
          startIndex += PAGE_SIZE;
        }
      }

      log.info("[executeDataPipeline] completed!");
    } catch (Exception e) {
      log.error("[executeDataPipeline] error - " + e);
      throw new CustomException(DataExceptionType.SCHEDULING_FAILED);
    }
  }
}
