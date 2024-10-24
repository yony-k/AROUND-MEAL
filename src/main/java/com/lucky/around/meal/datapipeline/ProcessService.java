package com.lucky.around.meal.datapipeline;

import java.util.concurrent.BlockingDeque;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {

  private final DataQueue dataQueue;

  @Async
  public void processData() {
    try {
      while (true) {
        // 수집한 데이터 가져오기
        RawData rawData = dataQueue.getCollectQueue().take();
        Thread.sleep(2000);

        // 데이터 가공하기
        ParsedData parsedData = new ParsedData("(Processed) " + rawData.getData());
        log.info("가공: " + parsedData.getData());

        // 가공된 데이터 큐에 넣기
        BlockingDeque<ParsedData> processQueue = dataQueue.getProcessQueue();
        processQueue.put(parsedData);
        log.info("가공 큐 크기: " + processQueue.size());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
