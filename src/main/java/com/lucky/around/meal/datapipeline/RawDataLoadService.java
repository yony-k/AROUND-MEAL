package com.lucky.around.meal.datapipeline;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataLoadService {

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

  @Value("${API_MAX_INDEX}")
  private int MAX_INDEX;

  public synchronized void executeRawDataLoad() {
    log.info("[executeRawDataLoad] 데이터 읽어오기 실행");
    //        throw new RuntimeException("데이터 읽어오기에서 일부러 발생시킨 예외입니다.");
  }
}
