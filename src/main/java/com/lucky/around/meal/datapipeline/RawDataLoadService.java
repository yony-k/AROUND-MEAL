package com.lucky.around.meal.datapipeline;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class RawDataLoadService {

  private final WebClient.Builder webClientBuilder;
  private final RawRestaurantRepository rawRestaurantRepository;
  private final ObjectMapper objectMapper;

  @Value("${API_BASE_URL}")
  private String BASE_URL;

  @Value("${API_KEY}")
  private String KEY;

  @Value("${API_SERVICE_NAME}")
  private String SERVICE_NAME;

  @Value("${API_FORMAT_TYPE}")
  private String FORMAT_TYPE;

  public synchronized void executeRawDataLoad(int startIndex, int endIndex) {
    log.info("[executeRawDataLoad] 데이터 읽어오기 실행");

    try {
      String responseData = fetchData(startIndex, endIndex).block();

      JsonNode rootNode = objectMapper.readTree(responseData);
      JsonNode rowNodes = rootNode.path(SERVICE_NAME).path("row");

      for (JsonNode rowNode : rowNodes) {
        String id = rowNode.path("MGTNO").asText("");
        String jsonData = rowNode.toString();

        saveRawData(id, jsonData);
      }
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
