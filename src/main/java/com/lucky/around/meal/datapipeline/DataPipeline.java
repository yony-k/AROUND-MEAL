package com.lucky.around.meal.datapipeline;

import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipeline {

  private final CollectService collectService;
  private final ProcessService processService;
  private final SaveService saveService;

  @Bean
  public ApplicationRunner initializer() {
    return args -> {
      runDataPipeline();
    };
  }

  @Async("taskExecutor")
  @Scheduled(cron = "0 0 0 * * ?")
  public void runDataPipeline() throws InterruptedException {
    Instant startTime = Instant.now();

    // 각 단계별 비동기 작업 시작
    collectService.collectData();
    processService.processData();
    saveService.saveData();

    Thread.sleep(1000);
    Instant endTime = Instant.now();
    long duration = Duration.between(startTime, endTime).toMillis();
    log.info("데이터 파이프라인 소요 시간 : " + duration);
  }
}
