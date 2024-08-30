package com.lucky.around.meal.datapipeline;

import java.io.IOException;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataSaveService {

  private final WebClient.Builder webClientBuilder;
  private final RawRestaurantRepository rawRestaurantRepository;
  private final DataProcessingService dataProcessingService;
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

  private final int MAX_INDEX = 499; // API 어디까지 부를지 결정 (원래는 59만 정도)

  @PostConstruct
  public void init() { // 애플리케이션 시작 후 1번 실행
    log.info("[init] 최초 API 호출입니다.");
    processRawDataSave();
  }

  @Transactional
  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시 실행
  //  @Scheduled(cron = "0 */3 * * * *") // 3분마다 실행
  public synchronized void executeRawDataRead() {
    log.info("[scheduler] API 재호출입니다.");
    processRawDataSave();
  }

  public void processRawDataSave() {
    try {
      int startIndex = 1;
      while (startIndex < MAX_INDEX) {
        int endIndex = startIndex + PAGE_SIZE - 1;
        String responseData = fetchData(startIndex, endIndex).block();

        JsonNode rootNode = objectMapper.readTree(responseData);
        JsonNode rowNodes = rootNode.path(SERVICE_NAME).path("row");

        for (JsonNode rowNode : rowNodes) {
          String id = rowNode.path("MGTNO").asText("");
          String jsonData = rowNode.toString();

          saveRawData(id, jsonData);
        }

        startIndex += PAGE_SIZE;
      }

      dataProcessingService.dataProcessing(); // 데이터 과정 단계로 넘어가기
    } catch (IOException e) {
      log.error("[processRawDataSave] I/O error - ", e);
    } catch (WebClientResponseException e) {
      log.error(
          "[processRawDataSave] API call error - Status: {}, Body: {}",
          e.getStatusCode(),
          e.getResponseBodyAsString(),
          e);
    } catch (Exception e) {
      log.error("[processRawDataSave] Unexpected error - ", e);
    }
  }

  public Mono<String> fetchData(int startIndex, int endIndex) {
    String uri =
        String.format("/%s/%s/%s/%s/%s", KEY, FORMAT_TYPE, SERVICE_NAME, startIndex, endIndex);
    return webClientBuilder
        .baseUrl(BASE_URL)
        .build()
        .get()
        .uri(uri)
        .retrieve()
        .bodyToMono(String.class);
  }

  public void saveRawData(String id, String jsonData) {
    RawRestaurant existedRawRestaurant = rawRestaurantRepository.findById(id).orElse(null);

    String newHash = HashUtil.generateSHA256Hash(jsonData);

    if (existedRawRestaurant == null || !existedRawRestaurant.getHash().equals(newHash)) {
      RawRestaurant rawRestaurant =
          RawRestaurant.builder().id(id).jsonData(jsonData).isUpdated(true).hash(newHash).build();
      rawRestaurantRepository.save(rawRestaurant);
      log.info(
          "[saveRawData] Saved data - id : {}, new entry: {}", id, existedRawRestaurant == null);
    }
  }
}
