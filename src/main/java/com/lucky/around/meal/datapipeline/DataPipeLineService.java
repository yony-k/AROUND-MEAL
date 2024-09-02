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

  @Value("${API_MAX_INDEX}")
  private int MAX_INDEX;

  @Value("${API_PAGE_SIZE}")
  private int PAGE_SIZE;

  private int rawDataLoadRetryCount;
  private int dataProcessRetryCount;

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
    boolean pipelineSuccess = true;

    while (startIndex <= MAX_INDEX) {
      int endIndex = startIndex + PAGE_SIZE - 1;

      /* 데이터 읽어오기 */
      boolean isRawDataLoaded = false;
      rawDataLoadRetryCount = 1; // 재시도 횟수 초기화
      while (!isRawDataLoaded && rawDataLoadRetryCount <= MAX_RETRY_COUNT) {
        try {
          rawDataLoadService.executeRawDataLoad(startIndex, endIndex);
          log.info("[success] 데이터 읽어오기 ({}번째 시도).", dataProcessRetryCount);
          isRawDataLoaded = true;
        } catch (Exception e) {
          log.error("[fail] 데이터 읽어오기 ({}번째 시도).", dataProcessRetryCount, e);
          rawDataLoadRetryCount++;
          if (rawDataLoadRetryCount > MAX_RETRY_COUNT) {
            log.error("[fail] 데이터 읽어오기 최대 재시도 횟수 초과", e);
            pipelineSuccess = false;
            break;
          }
          sleepBeforeRetry();
        }
      }

      if (!pipelineSuccess) break;

      /* 데이터 가공하기 */
      dataProcessRetryCount = 1; // 재시도 횟수 초기화
      boolean isDataProcessed = false;
      while (!isDataProcessed && dataProcessRetryCount <= MAX_RETRY_COUNT) {
        try {
          dataProcessService.executeDataProcess(PAGE_SIZE);
          log.info("[success] 데이터 가공하기 ({}번째 시도).", dataProcessRetryCount);
          isDataProcessed = true;
        } catch (Exception e) {
          log.error("[fail] 데이터 가공하기 ({}번째 시도).", dataProcessRetryCount, e);
          dataProcessRetryCount++;
          if (dataProcessRetryCount > MAX_RETRY_COUNT) {
            log.error("[fail] 데이터 가공하기 최대 재시도 횟수 초과", e);
            pipelineSuccess = false;
            break;
          }
          sleepBeforeRetry();
        }
      }

      if (!pipelineSuccess) break;

      // 다음 데이터를 처리하기 위한 시작 인덱스 업데이트
      startIndex += PAGE_SIZE;
    }
  }

  private void sleepBeforeRetry() {
    try {
      log.info("[sleep] 5초 후, 재실행");
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      log.error("[sleepBeforeRetry]", e);
      Thread.currentThread().interrupt();
    }
  }
}
