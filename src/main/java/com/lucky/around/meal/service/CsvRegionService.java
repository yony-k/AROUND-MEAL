package com.lucky.around.meal.service;

import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.entity.Region;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.CsvRegionExceptionType;
import com.lucky.around.meal.repository.RegionRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class CsvRegionService {

  @Autowired private RegionRepository regionRepository;

  @PostConstruct
  public void importCsvToDatabase() {
    /*
    저장 경로 resources/region/sgg_lat_lon.csv
    통일성을 위해 Region 엔티티에 맞춰 csv 파일 데이터 헤더 이름을 dosi, sigungu로 변경해두었습니다.
     */
    ClassPathResource resource = new ClassPathResource("region/sgg_lat_lon.csv");

    // 해당 csv 파일 존재 확인
    if (!resource.exists()) {
      throw new CustomException(CsvRegionExceptionType.FILE_NOT_FOUNDED);
    }

    try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
      String[] nextLine;
      reader.readNext(); // 헤더 스킵

      while ((nextLine = reader.readNext()) != null) {
        try {
          // CSV 파일의 각 줄을 읽어 Region 엔티티 생성
          Region region =
              Region.builder()
                  .dosi(nextLine[0]) // CSV 파일의 열 인덱스 : 도시, 시군구, 경도, 위도
                  .sigungu(nextLine[1])
                  .lon(Double.parseDouble(nextLine[2]))
                  .lat(Double.parseDouble(nextLine[3]))
                  .build();

          regionRepository.save(region); // 저장
        } catch (NumberFormatException e) {
          // todo: 예외처리 세분화 필요. - 실행을 위한 임시 예외처리
          // 숫자 변환 오류 처리 - 위도 경도에 숫자가 아닌 값
          throw new CustomException(CsvRegionExceptionType.FILE_READ_ERROR);
        } catch (ArrayIndexOutOfBoundsException e) {
          // 배열 인덱스 범위 오류 - 값 부족
          throw new CustomException(CsvRegionExceptionType.FILE_READ_ERROR);
        }
      }
    } catch (IOException e) {
      // 파일 읽기 오류
      throw new CustomException(CsvRegionExceptionType.FILE_READ_ERROR);
    } catch (CsvValidationException e) {
      // CSV 유효성 검사 오류
      throw new CustomException(CsvRegionExceptionType.FILE_READ_ERROR);
    }
  }
}
