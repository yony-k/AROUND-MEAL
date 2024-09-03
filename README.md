## 프로젝트 개요

### 기술 스택
![java](https://img.shields.io/badge/Java-17-blue?logo=java)
![spring-boot](https://img.shields.io/badge/SpringBoot-3.3.3-grren?logo=springboot)
![postgresql](https://img.shields.io/badge/PostgreSQL-16.4-blue?logo=postgresql)
![redis](https://img.shields.io/badge/Redis-7.4-red?logo=redis)

### 요구사항
[위치 기반 맛집 추천 서비스](https://bow-hair-db3.notion.site/f2c5e47f67254726ba0ef8d894f7e8b9#f62425d8700b485da11338e3b0eb06c4)

### 팀 구성

| 이름  | 담당                                    |
|-----|-----------------------------------------|
| 유하진 | 데이터 파이프라인 구축(수집, 전처리, 저장, 자동화) |
| 김성은 | 통계 API (맛집 목록 조회), 점심 추천 Webhook   |
| 김연희 | 사용자 회원가입 API, 사용자 로그인 API, 사용자 정보 API, 인기있는 맛집만 캐싱|
| 안소나 | 시군구 목록 조회 API, 맛집 평가 생성 API         |
| 유서정 | 사용자 설정(좌표, 서비스 수신 여부) 업데이트 API, 맛집 상세정보 API, 조회수 N 이상만 캐싱|

</br>

## 프로젝트 관리

<details>
<summary><strong>일정</strong></summary>
  
| 날짜 | 활동 |
| --- | --- |
| 24.08.27 (화) | 역할 분담, 기술 선택  |
| ~ 24.08.30 (금) | 요구사항 기능 개발, 추가 요구사항 분담 |
| ~ 24.09.01 (일)| 추가 요구사항 개발 |
| 24.09.01 (월) | README.md 작성 및 추가 요구사항 점검|
</details>

<details>
<summary><strong>협업 라이프사이클</strong></summary>

1. 브랜치 생성
2. 코드 작성
3. PR 생성
4. 리뷰 요청
5. dev 브랜치로 Merge
</details>

<details>
<summary><strong>이슈 관리</strong></summary>
<img src=https://github.com/user-attachments/assets/f74c4fb2-15ac-4a68-b040-5241ce8d1e29>

</details>

<details>
<summary><strong>컨벤션</strong></summary>

- **Branch**
    - **전략**

      | Branch Type | Description |
      | --- | --- |
      | `dev` | 주요 개발 branch, `main`으로 merge 전 거치는 branch |
      | `feature` | 각자 개발할 branch, 기능 단위로 생성하기, 할 일 issue 등록 후 branch 생성 및 작업 |

    - **네이밍**
        - `{header}/#{issue number}`
        - 예) `feat/#1`

- **커밋 메시지 규칙**
    ```bash
    > [HEADER] : 기능 요약
    
    - [CHORE]: 내부 파일 수정
    - [FEAT] : 새로운 기능 구현
    - [ADD] : FEAT 이외의 부수적인 코드 추가, 라이브러리 추가, 새로운 파일 생성 시
    - [FIX] : 코드 수정, 버그, 오류 해결
    - [DEL] : 쓸모없는 코드 삭제
    - [DOCS] : README나 WIKI 등의 문서 개정
    - [MOVE] : 프로젝트 내 파일이나 코드의 이동
    - [RENAME] : 파일 이름의 변경
    - [MERGE]: 다른 브렌치를 merge하는 경우
    - [STYLE] : 코드가 아닌 스타일 변경을 하는 경우
    - [INIT] : Initial commit을 하는 경우
    - [REFACTOR] : 로직은 변경 없는 클린 코드를 위한 코드 수정
    
    ex) [FEAT] 게시글 목록 조회 API 구현
    ex) [FIX] 내가 작성하지 않은 리뷰 볼 수 있는 버그 해결
    ```

- **Issue**
    ```bash
    🍚 Description
    <!-- 진행할 작업을 설명해주세요 -->
    
    🍚 To-do
    <!-- 작업을 수행하기 위해 해야할 태스크를 작성해주세요 -->
    [ ] todo1
    
    🍚 ETC
    <!-- 특이사항 및 예정 개발 일정을 작성해주세요 -->
    ```

- **PR**
  - **규칙**
    - branch 작업 완료 후 PR 보내기
    - 항상 local에서 충돌 해결 후 remote에 올리기
    - PR 후 디스코드에 공유하기
    - 당일 PR은 당일에 리뷰하기
    - 최소 2명 이상의 동의를 받으면 merge하기
    - review 반영 후, 본인이 merge
    ```bash
        > [MERGE] {브랜치이름}/{#이슈번호}
        ex) [MERGE] setting/#1
    ```
  - **Template**
    ```bash
    🍚 Description
    <!-- 진행할 작업을 설명해주세요 -->
    
    🍚 To-do
    <!-- 작업을 수행하기 위해 해야할 태스크를 작성해주세요 -->
    [ ] todo1
    
    🍚 ETC
    <!-- 특이사항 및 예정 개발 일정을 작성해주세요 -->
    ```
</details>

</br>

## 기술 문서

### 아키텍처
<img width="707" alt="architecture" src="https://github.com/user-attachments/assets/bea07fac-77ae-4fee-8e85-9f0e20905882">


### API 명세서

▶️ [API 명세서 자세히보기](https://www.notion.so/API-eedd570467c7427782b8b71a10ae4001?pvs=4)

![api](https://github.com/user-attachments/assets/954958e8-8c49-4b3d-b3e0-b0bfc014eb7a)



</br>

<details>
<summary><strong>ERD</strong></summary>
<img src=https://github.com/user-attachments/assets/4fc9a113-aee1-4cd9-9f6f-8ec9dab376a8>
</details>

<details>
<summary><strong>디렉토리 구조 - 도메인이 많지 않아 기능별로 패키지를 나누었습니다.</strong></summary>


```bash
├── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── lucky
    │   │           └── around
    │   │               └── meal
    │   │                   ├── Application.java
    │   │                   ├── cache
    │   │                   │   ├── entity
    │   │                   │   │   └── RestaurantForRedis.java
    │   │                   │   ├── repository
    │   │                   │   │   └── RestaurantForRedisRepository.java
    │   │                   │   └── service
    │   │                   │       ├── RatingCountService.java
    │   │                   │       └── ViewCountService.java
    │   │                   ├── common
    │   │                   │   ├── WebClientConfig.java
    │   │                   │   ├── config
    │   │                   │   │   ├── CacheConfig.java
    │   │                   │   │   ├── GeometryConfig.java
    │   │                   │   │   ├── RedisConfig.java
    │   │                   │   │   └── SpringSecurityConfig.java
    │   │                   │   ├── redis
    │   │                   │   │   └── RedisRepository.java
    │   │                   │   ├── security
    │   │                   │   │   ├── details
    │   │                   │   │   │   ├── PrincipalDetails.java
    │   │                   │   │   │   └── PrincipalDetailsService.java
    │   │                   │   │   ├── filter
    │   │                   │   │   │   ├── JwtAuthenticationFilter.java
    │   │                   │   │   │   └── JwtAuthorizationFilter.java
    │   │                   │   │   ├── handler
    │   │                   │   │   │   ├── CustomAccessDeniedHandler.java
    │   │                   │   │   │   ├── CustomAuthenticationEntryPoint.java
    │   │                   │   │   │   ├── CustomLogoutHandler.java
    │   │                   │   │   │   └── CustomLogoutSuccessHandler.java
    │   │                   │   │   ├── record
    │   │                   │   │   │   └── JwtRecord.java
    │   │                   │   │   ├── redis
    │   │                   │   │   │   ├── RefreshToken.java
    │   │                   │   │   │   └── RefreshTokenRepository.java
    │   │                   │   │   └── util
    │   │                   │   │       ├── CookieProvider.java
    │   │                   │   │       └── JwtProvider.java
    │   │                   │   └── util
    │   │                   │       └── GeometryUtil.java
    │   │                   ├── controller
    │   │                   │   ├── LocationController.java
    │   │                   │   ├── MemberController.java
    │   │                   │   ├── RatingController.java
    │   │                   │   ├── RegionController.java
    │   │                   │   ├── RestaurantController.java
    │   │                   │   ├── dto
    │   │                   │   │   ├── GetRestaurantsDto.java
    │   │                   │   │   └── MemberDto.java
    │   │                   │   ├── record
    │   │                   │   │   ├── LoginRecord.java
    │   │                   │   │   ├── RegionRecord.java
    │   │                   │   │   └── RegisterRecord.java
    │   │                   │   ├── request
    │   │                   │   │   ├── MemberLocationRequestDto.java
    │   │                   │   │   ├── RatingRequestDto.java
    │   │                   │   │   ├── RestaurantDetailsRequestDto.java
    │   │                   │   │   └── StaticLocationRequestDto.java
    │   │                   │   └── response
    │   │                   │       ├── LocationResponseDto.java
    │   │                   │       ├── RatingResponseDto.java
    │   │                   │       ├── RestaurantDetailResponseDto.java
    │   │                   │       └── StaticLocationResponseDto.java
    │   │                   ├── datapipeline
    │   │                   │   ├── DataPipeLineService.java
    │   │                   │   ├── DataProcessService.java
    │   │                   │   ├── HashUtil.java
    │   │                   │   ├── RawDataLoadService.java
    │   │                   │   ├── RawRestaurant.java
    │   │                   │   └── RawRestaurantRepository.java
    │   │                   ├── entity
    │   │                   │   ├── Member.java
    │   │                   │   ├── Rating.java
    │   │                   │   ├── Region.java
    │   │                   │   ├── Restaurant.java
    │   │                   │   └── enums
    │   │                   │       ├── Category.java
    │   │                   │       └── MemberRole.java
    │   │                   ├── exception
    │   │                   │   ├── CustomException.java
    │   │                   │   ├── CustomExceptionHandler.java
    │   │                   │   └── exceptionType
    │   │                   │       ├── CommonExceptionType.java
    │   │                   │       ├── CsvRegionExceptionType.java
    │   │                   │       ├── DataExceptionType.java
    │   │                   │       ├── ExceptionType.java
    │   │                   │       ├── MemberExceptionType.java
    │   │                   │       ├── RegionExceptionType.java
    │   │                   │       ├── RegisterExceptionType.java
    │   │                   │       ├── RestaurantExceptionType.java
    │   │                   │       └── SecurityExceptionType.java
    │   │                   ├── repository
    │   │                   │   ├── MemberRepository.java
    │   │                   │   ├── RatingRepository.java
    │   │                   │   ├── RegionRepository.java
    │   │                   │   └── RestaurantRepository.java
    │   │                   └── service
    │   │                       ├── CsvRegionService.java
    │   │                       ├── JwtService.java
    │   │                       ├── LocationService.java
    │   │                       ├── MemberService.java
    │   │                       ├── RatingService.java
    │   │                       ├── RegionService.java
    │   │                       └── RestaurantService.java
    │   └── resources
    │       ├── application.properties
    │       ├── application.yml
    │       └── region
    │           └── sgg_lat_lon.csv
    └── test
        └── java
            └── com
                └── lucky
                    └── around
                        └── meal
                            ├── ApplicationTests.java
                            ├── cache
                            │   ├── RatingCountServiceTest.java
                            │   └── RestaurantRepositoryTest.java
                            ├── common
                            │   └── security
                            │       └── util
                            │           ├── CookieProviderTest.java
                            │           └── JwtProviderTest.java
                            └── service
                                ├── LocationServiceTest.java
                                ├── MemberServiceTest.java
                                └── RestaurantServiceTest.java
```
</details>

</br>

## 기능 구현

### RESTful API
#### 사용자 회원가입 api

#### 사용자 로그인 api

#### 사용자 설정 업데이트 api

#### 사용자 정보 api

#### 시군구 목록 api
- DB 내 존재하는 시군구 목록을 전체 조회하는 페이지
- 시군구 정보가 담긴 csv 파일을 프로젝트 실행 시 DB에 바로 업로드 하는 기능 구현
- 긴시간 변동 없는 성격을 지닌 데이터이기에 Redis와 연동한 캐싱을 진행하여 전체 조회 속도 개선
#### 맛집 목록 api

#### 맛집 상세정보 api

#### 맛집 평가 생성 api
- 로그인한 유저가 특정 식당에 대한 평가를 작성하는 페이지
- 평가가 생성 되면 해당 맛집의 평점이 업데이트 되고, 전체 평점 평균을 계산하여 업데이트하는 로직 구현
- 동일한 유저가 한 식당에 대해 중복 평가가 불가능하도록 예외처리

### 데이터 파이프라인
- 공공데이터 수집
- 데이터 가공
- 데이터 저장
- 데이터 파이프라인 자동화

### Webhook 
- Discord Webhook 을 활용한 점심 추천 서비스

### 기타
시군구 데이터 업로드
인기있는 맛집 캐싱
조회수 N회 이상 맛집 캐싱
맛집 상세정보 캐싱

</br>

## 트러블 슈팅
- 캐싱 전략
- 데이터 파이프라인 전략
- postgis
