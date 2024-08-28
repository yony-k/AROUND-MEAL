package com.lucky.around.meal.service.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

  // 서비스 메서드를 동기적으로 호출하고 결과를 저장
  public String getResult() {
    return fetchData().block();
  }
}
