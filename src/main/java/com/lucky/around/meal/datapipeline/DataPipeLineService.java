package com.lucky.around.meal.datapipeline;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipeLineService {
  private static final int MAX_RETRY_COUNT = 3;
  private static final int RETRY_DELAY_MS = 5000;

  @Value("${API_MAX_INDEX}")
  private int MAX_INDEX;

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  private final RawDataLoadService rawDataLoadService;
  private final DataProcessService dataProcessService;

  @PostConstruct
  public void init() {
    log.info("[init] 데이터 파이프라인");
    executeDataPipeLine();
  }

  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시 실행
  public void executeDataPipeLine() {
    log.info("[execute] 데이터 파이프라인");

    int startIndex = 1;
    while (startIndex <= MAX_INDEX) {
      int endIndex = startIndex + PAGE_SIZE - 1;

      if (!loadRawData(startIndex, endIndex)) {
        break;
      }

      if (!processData()) {
        break;
      }

      startIndex += PAGE_SIZE;
    }
  }

  private boolean loadRawData(int startIndex, int endIndex) {
    int retryCount = 1; // 재시도 횟수 초기화
    while (retryCount <= MAX_RETRY_COUNT) {
      try {
        rawDataLoadService.executeRawDataLoad(startIndex, endIndex);
        log.info("[success] 데이터 읽어오기 ({}번째 시도).", retryCount);
        return true;
      } catch (Exception e) {
        log.error("[fail] 데이터 읽어오기 ({}번째 시도).", retryCount, e);
        retryCount++;
        if (retryCount > MAX_RETRY_COUNT) {
          log.error("[fail] 데이터 읽어오기 최대 재시도 횟수 초과", e);
          return false;
        }
        sleepBeforeRetry();
      }
    }
    return false; // 실제로는 도달하지 않는 코드
  }

  private boolean processData() {
    int retryCount = 1; // 재시도 횟수 초기화
    while (retryCount <= MAX_RETRY_COUNT) {
      try {
        dataProcessService.executeDataProcess(PAGE_SIZE);
        log.info("[success] 데이터 가공하기 ({}번째 시도).", retryCount);
        return true;
      } catch (Exception e) {
        log.error("[fail] 데이터 가공하기 ({}번째 시도).", retryCount, e);
        retryCount++;
        if (retryCount > MAX_RETRY_COUNT) {
          log.error("[fail] 데이터 가공하기 최대 재시도 횟수 초과", e);
          return false;
        }
        sleepBeforeRetry();
      }
    }
    return false; // 실제로는 도달하지 않는 코드
  }

  private void sleepBeforeRetry() {
    try {
      log.info("[sleep] {}초 후, 재실행", RETRY_DELAY_MS);
      Thread.sleep(RETRY_DELAY_MS);
    } catch (InterruptedException e) {
      log.error("[sleepBeforeRetry]", e);
      Thread.currentThread().interrupt();
    }
  }
}
