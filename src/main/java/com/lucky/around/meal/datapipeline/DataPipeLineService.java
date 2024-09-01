package com.lucky.around.meal.datapipeline;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipeLineService {

  private boolean isRawDataSaveServiceSuccess = false;

  private final RawDataSaveService rawDataSaveService;
  private final DataProcessingService dataProcessingService;

  @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시 실행
  //  @Scheduled(cron = "0 */3 * * * *") // 3분마다 실행
  public void executeDataPipeLine() {
    try {
      if (!isRawDataSaveServiceSuccess) {
        rawDataSaveService.executeRawDataRead();
        isRawDataSaveServiceSuccess = true;
      }
      dataProcessingService.dataProcessing(); // dataProcessing 메소드명 변경하기
    } catch (Exception e) {
      log.error("[executeDataPipeLine] dataProcessingService - error :", e);
      isRawDataSaveServiceSuccess = true;
    }
  }
}
