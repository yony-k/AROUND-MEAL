package com.lucky.around.meal.service.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.DataExceptionType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DataService {

  private final WebClient.Builder webClientBuilder;

  @Value("${API_BASE_URL}")
  private String BASE_URL;

  @Value("${API_KEY}")
  private String KEY;

  @Value("${API_SERVICE_NAME}")
  private String SERVICE_NAME;

  @Value("${API_FORMAT_TYPE}")
  private String FORMAT_TYPE;

  @Autowired
  public DataService(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  // API 호출
  public Mono<String> fetchData() {
    String startIndex = "1";
    String endIndex = "1";
    String uri =
        String.format("/%s/%s/%s/%s/%s", KEY, FORMAT_TYPE, SERVICE_NAME, startIndex, endIndex);

    log.info("[fetchData] API 호출 URI: " + uri);

    WebClient webClient = webClientBuilder.baseUrl(BASE_URL).build();

    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(uri).build())
        .retrieve()
        .bodyToMono(String.class);
  }

  protected Map<String, String> parseData(String data) {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> parsedData = new HashMap<>();

    try {
      JsonNode rootNode = objectMapper.readTree(data);
      JsonNode rowNode = rootNode.path(SERVICE_NAME).path("row").get(0);

      // 데이터 추출
      String id = rowNode.path("MGTNO").asText();
      String restaurantName = rowNode.path("BPLCNM").asText();
      String category = rowNode.path("UPTAENM").asText();
      String x = rowNode.path("X").asText();
      String y = rowNode.path("Y").asText();

      String jibunAddress = rowNode.path("SITEWHLADDR").asText();
      String[] jibunAddresses = jibunAddress.split(" ", 3);
      String dosi = jibunAddresses[0];
      String sigungu = jibunAddresses[1];
      String jibunDetailAddress = jibunAddresses[2];

      String doroAddress = rowNode.path("RDNWHLADDR").asText();
      String[] doroAddresses = doroAddress.split(" ", 3);
      String doroDetailAddress = doroAddresses[2];

      // 결과 데이터
      parsedData.put("id", id);
      parsedData.put("restaurantName", restaurantName);
      parsedData.put("category", category);
      parsedData.put("x", x);
      parsedData.put("y", y);
      parsedData.put("dosi", dosi);
      parsedData.put("sigungu", sigungu);
      parsedData.put("jibunDetailAddress", jibunDetailAddress);
      parsedData.put("doroDetailAddress", doroDetailAddress);

    } catch (Exception e) {
      throw new CustomException(DataExceptionType.DATA_PARSING_FAILED);
    }

    return parsedData;
  }

  // 서비스 메서드를 동기적으로 호출하고 결과를 저장
  public String getResult() {
    String responseData = fetchData().block();
    Map<String, String> parsedData = parseData(responseData);
    log.info("parsedData: " + parsedData);
    return "ok";
  }
}
