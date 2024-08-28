package com.lucky.around.meal.service.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.around.meal.entity.Region;
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
  public Mono<String> fetchData() {
    String startIndex = "1";
    String endIndex = "1";
    String uri =
        String.format("/%s/%s/%s/%s/%s", KEY, FORMAT_TYPE, SERVICE_NAME, startIndex, endIndex);

    WebClient webClient = webClientBuilder.baseUrl(BASE_URL).build();

    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(uri).build())
        .retrieve()
        .bodyToMono(String.class);
  }

  // 데이터 파싱
  private Map<String, String> parseData(String data) {
    try {
      JsonNode rootNode = objectMapper.readTree(data);
      JsonNode rowNode = rootNode.path(SERVICE_NAME).path("row").get(0);

      // 필요한 데이터 추출 후, Map 으로 만들기
      Map<String, String> parsedData = new HashMap<>();
      parsedData.put("id", rowNode.path("MGTNO").asText());
      parsedData.put("restaurantName", rowNode.path("BPLCNM").asText());
      parsedData.put("category", rowNode.path("UPTAENM").asText());
      parsedData.put("restaurantTel", rowNode.path("SITETEL").asText());
      parsedData.put("x", rowNode.path("X").asText());
      parsedData.put("y", rowNode.path("Y").asText());

      String jibunAddress = rowNode.path("SITEWHLADDR").asText();
      String[] jibunAddresses = jibunAddress.split(" ", 3);
      parsedData.put("dosi", jibunAddresses[0]);
      parsedData.put("sigungu", jibunAddresses[1]);
      parsedData.put("jibunDetailAddress", jibunAddresses[2]);

      String doroAddress = rowNode.path("RDNWHLADDR").asText();
      String[] doroAddresses = doroAddress.split(" ", 3);
      parsedData.put("doroDetailAddress", doroAddresses[2]);

      return parsedData;
    } catch (Exception e) {
      throw new CustomException(DataExceptionType.DATA_PARSING_FAILED);
    }
  }

  // 데이터 저장
  public void saveData(Map<String, String> parsedData) {
    Region region = saveRegion(parsedData.get("dosi"), parsedData.get("sigungu"));

    Restaurant restaurant =
        Restaurant.builder()
            .id(parsedData.get("id"))
            .restaurantName(parsedData.get("restaurantName"))
            .restaurantTel(parsedData.get("restaurantTel"))
            .jibunDetailAddress(parsedData.get("jibunDetailAddress"))
            .doroDetailAddress(parsedData.get("doroDetailAddress"))
            .region(region)
            .category(Category.of(parsedData.get("category")))
            .lon(Double.parseDouble(parsedData.get("x")))
            .lat(Double.parseDouble(parsedData.get("y")))
            .build();

    restaurantRepository.save(restaurant);
  }

  private Region saveRegion(String dosi, String sigungu) {
    Region region = regionRepository.findByDosiAndSigungu(dosi, sigungu).orElse(null);
    if (region == null) {
      Region newRegion = Region.builder().dosi(dosi).sigungu(sigungu).build();
      return regionRepository.save(newRegion);
    }

    return region;
  }

  // 스케줄러 설정
  //  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1신
  @Scheduled(fixedRate = 180_000) // 테스트용 3분 마다 실행
  public void executeDataPipeline() {
    try {
      log.info("[executeDataPipeline] started!");
      String responseDta = fetchData().block();
      Map<String, String> parsedData = parseData(responseDta);
      saveData(parsedData);
      log.info("[executeDataPipeline] completed!");
    } catch (Exception e) {
      log.error("[executeDataPipeline] error - " + e.getMessage());
      throw new CustomException(DataExceptionType.SCHEDULING_FAILED);
    }
  }
}
