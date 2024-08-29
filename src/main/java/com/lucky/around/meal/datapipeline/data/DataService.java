package com.lucky.around.meal.datapipeline.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.entity.Restaurant;
import com.lucky.around.meal.entity.enums.Category;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.DataExceptionType;
import com.lucky.around.meal.repository.RegionRepository;
import com.lucky.around.meal.repository.RestaurantRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DataService {

  private final WebClient.Builder webClientBuilder;
  private final RestaurantRepository restaurantRepository;
  private final RegionRepository regionRepository;
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

  @Autowired
  public DataService(
      WebClient.Builder webClientBuilder,
      RestaurantRepository restaurantRepository,
      RegionRepository regionRepository,
      ObjectMapper objectMapper) {
    this.webClientBuilder = webClientBuilder;
    this.restaurantRepository = restaurantRepository;
    this.regionRepository = regionRepository;
    this.objectMapper = objectMapper;
  }

  // 데이터 수집
  public Mono<String> fetchData(int startIndex, int endIndex) {
    try {
      String uri =
          String.format("/%s/%s/%s/%s/%s", KEY, FORMAT_TYPE, SERVICE_NAME, startIndex, endIndex);

      WebClient webClient = webClientBuilder.baseUrl(BASE_URL).build();

      return webClient
          .get()
          .uri(uriBuilder -> uriBuilder.path(uri).build())
          .retrieve()
          .bodyToMono(String.class);
    } catch (Exception e) {
      log.error("[fetchData] error - " + e);
    }

    return null;
  }

  // 데이터 파싱
  private List<Map<String, String>> parseData(String data) {
    try {
      JsonNode rootNode = objectMapper.readTree(data);
      JsonNode rowNodes = rootNode.path(SERVICE_NAME).path("row");

      // 필요한 데이터 추출 후, Map 으로 만들기
      return StreamSupport.stream(rowNodes.spliterator(), false)
          .map(
              rowNode -> {
                Map<String, String> parsedData = new HashMap<>();
                parsedData.put("id", getOrEmpty(rowNode, "MGTNO"));
                parsedData.put("restaurantName", getOrEmpty(rowNode, "BPLCNM"));
                parsedData.put("category", getOrEmpty(rowNode, "UPTAENM"));
                parsedData.put("restaurantTel", getOrEmpty(rowNode, "SITETEL"));
                parsedData.put("x", getOrEmpty(rowNode, "X"));
                parsedData.put("y", getOrEmpty(rowNode, "Y"));

                String jibunAddress = getOrEmpty(rowNode, "SITEWHLADDR");
                String[] jibunAddresses = splitAddress(jibunAddress);
                parsedData.put("dosi", jibunAddresses[0]);
                parsedData.put("sigungu", jibunAddresses[1]);
                parsedData.put("jibunDetailAddress", jibunAddresses[2]);

                String doroAddress = getOrEmpty(rowNode, "RDNWHLADDR");
                String[] doroAddresses = splitAddress(doroAddress);
                parsedData.put("doroDetailAddress", doroAddresses[2]);

                return parsedData;
              })
          .collect(Collectors.toList());

    } catch (Exception e) {
      log.info("[parseData] error - " + e);
      throw new CustomException(DataExceptionType.DATA_PARSING_FAILED);
    }
  }

  // 데이터가 있으면 그대로 반환, 없으면 "" 반환
  private String getOrEmpty(JsonNode node, String field) {
    String value = node.path(field).asText("");

    if (value.isEmpty()) {
      log.error(
          "node - " + node.path("MGTNO").asText("") + ", filed: " + field + ", value: " + value);
    }

    return value.isEmpty() ? "" : value;
  }

  // 주소 분할
  private String[] splitAddress(String address) {
    log.info("splitedAddress - " + Arrays.stream(new String[] {address}).toList());

    if (address == null || address.isEmpty()) {
      return new String[] {"", "", ""};
    }
    return address.split(" ", 3);
  }

  // 데이터 저장
  public void saveData(List<Map<String, String>> parsedDataList) {
    try {
      for (Map<String, String> parsedData : parsedDataList) {
        Restaurant restaurant =
            Restaurant.builder()
                .id(parsedData.get("id"))
                .restaurantName(parsedData.get("restaurantName"))
                .restaurantTel(parsedData.get("restaurantTel"))
                .jibunDetailAddress(parsedData.get("jibunDetailAddress"))
                .doroDetailAddress(parsedData.get("doroDetailAddress"))
                .dosi(parsedData.get("dosi"))
                .sigungu(parsedData.get("sigungu"))
                .category(Category.of(parsedData.get("category")))
                .lon(validateCoordinate(parsedData.get("x")))
                .lat(validateCoordinate(parsedData.get("y")))
                .build();
        restaurantRepository.save(restaurant);
      }
    } catch (Exception e) {
      log.error("[saveData] error - " + e);
    }
  }

  // "" 값 일 때, 0.0 넣어주기
  private Double validateCoordinate(String coordinate) {
    if (coordinate == null || coordinate.trim().isEmpty()) {
      return 0.0;
    }

    return Double.parseDouble(coordinate);
  }

  // 스케줄러 설정
  //  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시 실행
  @Scheduled(fixedRate = 900_000) // 테스트용
  public void executeDataPipeline() {
    try {
      log.info("[executeDataPipeline] started!");

      int startIndex = 1;
      boolean hasMoreData = true;

      while (hasMoreData) {
        int endIndex = startIndex + PAGE_SIZE - 1;
        String responseDta = fetchData(startIndex, endIndex).block();
        List<Map<String, String>> parsedDataList = parseData(responseDta);

        if (parsedDataList.isEmpty()) {
          hasMoreData = false;
        } else {
          saveData(parsedDataList);
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
