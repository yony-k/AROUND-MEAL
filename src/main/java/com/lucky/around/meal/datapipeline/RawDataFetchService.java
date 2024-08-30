package com.lucky.around.meal.datapipeline;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataFetchService {

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

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  public Mono<String> fetchData(int startIndex, int endIndex) {
    String uri =
        String.format("/%s/%s/%s/%s/%s", KEY, FORMAT_TYPE, SERVICE_NAME, startIndex, endIndex);
    WebClient webClient = webClientBuilder.baseUrl(BASE_URL).build();
    return webClient.get().uri(uri).retrieve().bodyToMono(String.class);
  }

  public void saveRawData(String id, String jsonData) {
    String hash = HashUtil.generateSHA256Hash(jsonData);
    RawRestaurant rawRestaurant =
        RawRestaurant.builder().id(id).jsonData(jsonData).isUpdated(false).hash(hash).build();
    rawRestaurantRepository.save(rawRestaurant);
  }

  //  @Scheduled(fixedRate = 900_000)
  public void executeDataFetch() {
    try {
      int startIndex = 1;

      while (startIndex < 499) {
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
