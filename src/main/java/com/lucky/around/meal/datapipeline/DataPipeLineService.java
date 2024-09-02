package com.lucky.around.meal.datapipeline;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipeLineService {
  private static final int MAX_RETRY_COUNT = 3;
  private int rawDataLoadRetryCount;
  private int dataProcessRetryCount;

  private final RawDataLoadService rawDataLoadService;
  private final DataProcessService dataProcessService;

  @PostConstruct
  public void init() {
    log.info("[init] 애플리케이션 시작 시 데이터 파이프라인 실행");
    executeDataPipeLine();
  }

  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시 실행
  //  @Scheduled(cron = "0 */1 * * * *") // 1분마다 실행
  public void executeDataPipeLine() {
    log.info("[executeDataPipeLine] 데이터 파이프라인 실행");

    // 데이터 읽어오기
    boolean isRawDataLoaded = false;
    rawDataLoadRetryCount = 1; // 재시도 횟수 초기화
    while (!isRawDataLoaded && rawDataLoadRetryCount < MAX_RETRY_COUNT) {
      try {
        rawDataLoadService.executeRawDataLoad();
        log.info("[rawDataLoadService] {}회 실행", rawDataLoadRetryCount);
        isRawDataLoaded = true;
      } catch (Exception e) {
        log.error("[executeDataPipeLine] 데이터 읽어오기 오류 발생 :", e);
        rawDataLoadRetryCount++;
        if (rawDataLoadRetryCount > MAX_RETRY_COUNT) {
          log.error("[executeDataPipeLine] 데이터 읽어오기 최대 재시도 횟수 초과 :", e);
          return;
        }
        sleepBeforeRetry();
      }
    }

    // 데이터 가공하기
    dataProcessRetryCount = 1; // 재시도 횟수 초기화
    boolean isDataProcessed = false;
    while (!isDataProcessed && dataProcessRetryCount < MAX_RETRY_COUNT) {
      try {
        dataProcessService.executeDataProcess();
        log.info("[dataProcessService] {}회 실행", dataProcessRetryCount);
        isDataProcessed = true;
      } catch (Exception e) {
        log.error("[executeDataPipeLine] 데이터 가공하기 오류 발생 :", e);
        dataProcessRetryCount++;
        if (dataProcessRetryCount > MAX_RETRY_COUNT) {
          log.error("[executeDataPipeLine] 데이터 가공하기 최대 재시도 횟수 초과 :", e);
          return;
        }
        sleepBeforeRetry();
      }
    }
  }

  private void sleepBeforeRetry() {
    try {
      log.info("[sleepBeforeRetry] 대기중");
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      log.error("[sleepBeforeRetry]", e);
      Thread.currentThread().interrupt();
    }
  }
}
