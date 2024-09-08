# 주변한끼 (Around Meal)
1. [프로젝트 개요](#1-프로젝트-개요)
2. [프로젝트 관리](#2-프로젝트-관리)
3. [기술 문서](#3-기술-문서)
4. [기능 구현](#4-기능-구현)
5. [트러블 슈팅](#5-트러블-슈팅)

## 1. 프로젝트 개요

### ⚙️ 기술 스택
![java](https://img.shields.io/badge/Java-17-blue?logo=java)
![spring-boot](https://img.shields.io/badge/SpringBoot-3.3.3-grren?logo=springboot)
![postgresql](https://img.shields.io/badge/PostgreSQL-16.4-blue?logo=postgresql)
![redis](https://img.shields.io/badge/Redis-7.4-red?logo=redis)

### ✔️ 요구사항
[위치 기반 맛집 추천 서비스](https://bow-hair-db3.notion.site/f2c5e47f67254726ba0ef8d894f7e8b9#f62425d8700b485da11338e3b0eb06c4)

### 👩🏻‍💻 팀 구성

| 이름  | 담당                                    |
|-----|-----------------------------------------|
| 유하진 | 데이터 파이프라인 구축(수집, 전처리, 저장, 자동화) |
| 김성은 | 통계 API (맛집 목록 조회), 점심 추천 Webhook   |
| 김연희 | 사용자 회원가입 API, 사용자 로그인 API, 사용자 정보 API, 인기있는 맛집만 캐싱|
| 안소나 | 시군구 목록 조회 API, 맛집 평가 생성 API         |
| 유서정 | 사용자 설정(좌표, 서비스 수신 여부) 업데이트 API, 맛집 상세정보 API, 조회수 N 이상만 캐싱|

</br>

## 2. 프로젝트 관리

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

## 3. 기술 문서

### 🛠️ 아키텍처
<img width="707" alt="architecture" src="https://github.com/user-attachments/assets/bea07fac-77ae-4fee-8e85-9f0e20905882">


### 📄 API 명세서

▶️ [API 명세서 자세히보기](https://www.notion.so/API-197df8e5668f42baa79c96ffac873a47?pvs=21)

| API 명칭 | HTTP 메서드 | 엔드포인트 | 설명 |
| --- | --- | --- | --- |
| **사용자 회원가입** | POST | `/api/register` | 새로운 사용자를 등록합니다. |
| **사용자 로그인** | POST | `/api/login` | 사용자를 로그인시킵니다. |
| **사용자 로그아웃** | POST | `/api/logout`  | 사용자를 로그아웃시킵니다. |
| **맛집 목록 조회** | GET | `/api/posts` | 맛집 목록을 조회합니다. |
| **맛집 평가 생성** | GET | `/api/posts/{id}` | 특정 게시물의 상세 정보를 조회합니다. |
| **시군구 목록 조회** | PUT | `/api/posts/{id}/like` | 게시물에 좋아요를 추가합니다. |
| **사용자 설정 업데이트** | PUT | `/api/posts/{id}/share` | 게시물을 공유합니다. |
| **맛집 상세정보 조회** | GET | `/api/stats` | 게시물 통계 정보를 조회합니다. |

</br>

<details>
<summary><strong>ERD</strong></summary>
<img src=https://github.com/user-attachments/assets/4fc9a113-aee1-4cd9-9f6f-8ec9dab376a8>
</details>

<details>
<summary><strong>디렉토리 구조 - 도메인이 많지 않아 계층별로 패키지를 나누었습니다.</strong></summary>


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

## 4. 기능 구현

### ⭐ 사용자 인증 시스템

#### ✨ 사용자 회원가입(담당: 김연희)
- 계정명 중복 검증 기능 구현
- 비밀번호 BCrypt 암호화 저장 기능 구현
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>계정명 중복 검증</strong></div>
        <div>이번 프로젝트는 사용자의 위치를 활용한 서비스가 주 목적이기 때문에 회원가입 정보 검증 과정은 간단하게 중복검증 기능만 구현했습니다.</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/controller/MemberController.java" target="_blank">MemberController</a></br>
    </div>
</details>

#### ✨ 사용자 로그인(담당: 김연희)
- 스프링 시큐리티 기능 구현
- JWT 방식 차용하여 로그인 시 AccessToken, RefreshToken 발급 기능 구현
- 핸들러를 사용한 필터 체인 내 예외처리 기능 구현
- RefreshToken으로 AccessToken, RefreshToken 재발급 기능 구현
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>스프링 시큐리티 구현</strong></div>
        <div>요청의 인증, 인가 처리를 간편하게 처리할 수 있기 때문에 스프링 시큐리티를 도입하였습니다. 로그인 처리를 전담하는 필터와, JWT 검증을 전담하는 필터를 커스텀하였고 각 필터에서 발생하는 예외는 예외의 종류에 따라 메세지를 달리하여 클라이언트에게 전달되도록 구현했습니다.</div>
        <div><strong>RefreshToken 발급 구현</strong></div>
        <div>AccessToken만으로는 보안상 취약하다고 판단되어 RefreshToken도 함께 발급하는 방식으로 구현했습니다. RefreshToken은 Redis에 저장하여 빠른 접근이 가능하도록 하였습니다.</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/tree/dev/src/main/java/com/lucky/around/meal/common/security" target="_blank">security</a></br>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/controller/MemberController.java" target="_blank">MemberController</a></br>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/service/MemberService.java" target="_blank">MemberService</a></br>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/service/JwtService.java" target="_blank">JwtService</a></br>
    </div>
</details>

#### ✨ 사용자 정보(담당: 김연희)
- 사용자 상세 정보 반환 기능 구현

<details>
    <summary>구현 코드</summary>
    <div>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/controller/MemberController.java" target="_blank">MemberController</a></br>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/service/MemberService.java" target="_blank">MemberService</a></br>
    </div>
</details>

#### ✨ 사용자 설정 업데이트(담당: 유서정)
- 채워주세요
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>

### ⭐ RESTful API

#### ✨ 시군구 목록(담당: 안소나)
- DB 내 존재하는 시군구 목록을 전체 조회하는 페이지
- 시군구 정보가 담긴 csv 파일을 프로젝트 실행 시 DB에 바로 업로드 하는 기능 구현
- 긴시간 변동 없는 성격을 지닌 데이터이기에 Redis와 연동한 캐싱을 진행하여 전체 조회 속도 개선
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>

#### ✨ 맛집 목록(담당: 김성은)
- 채워주세요
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>

#### ✨ 맛집 상세정보(담당: 안소나)
- 채워주세요
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>

#### ✨ 맛집 평가 생성(담당: 안소나)
- 로그인한 유저가 특정 식당에 대한 평가를 작성하는 페이지
- 평가가 생성 되면 해당 맛집의 평점이 업데이트 되고, 전체 평점 평균을 계산하여 업데이트하는 로직 구현
- 동일한 유저가 한 식당에 대해 중복 평가가 불가능하도록 예외처리

### ⭐ 데이터 파이프라인 (담당: 유하진)
- 공공데이터 OpenAPI 활용
- 데이터 파이프라인 (수집,가공, 저장) 구현
- 스케줄링을 통한 주기적인 데이터 파이프라인 실행
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>읽기/가공 책임분리</strong></div>
        <div>데이터 읽어오기 (API 호출 -> 원본 저장)과 데이터 가공하기 (원본 테이블 -> 가공 데이터 저장)로 책임을 분리했습니다. 데이터를 가공하는 도중 오류가 발생하더라도, 처음인 데이터 읽어오기부터 실행하지 않고 데이터 가공하기 과정만 재시도 됩니다. 또한 원본 테이블과 가공 테이블도 따로 관리되어, 원본 데이터를 그대로 보존하고 있으므로, 데이터를 다양하게 가공할 수 있습니다.</div>
        </br>
        <div><strong>해시와 플래그를 활용한 데이터 변경 감지</strong></div>
        <div>해시값을 비교하여 변경된 데이터를 식별합니다. 변경된 데이터는 변경 플래그로 명시합니다. 가공 테이블에는 변경 플래그로 표시된 데이터만 저장하도록 합니다. 데이터 저장 및 가공하는 처리 비용을 절감할 수 있습니다.</div>
        </br>
        <div><strong>데이터 파이프라인 비동기화</strong></div>
        <div>@Async 어노테이션을 사용하여 데이터 파이프라인 비동기처리할 수 있도록 하였습니다. 데이터 파이프라인이 진행되더라도 메인 스레드는 기다리지 않고 작업을 진행합니다.</div>
        </br>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/tree/dev/src/main/java/com/lucky/around/meal/datapipeline" target="_blank">데이터 파이프라인 패키지</a>
    </div>
</details>

### ⭐ Webhook
#### ✨ Discord Webhook 을 활용한 점심 추천 서비스(담당: 김성은)
- 채워주세요
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>

### ⭐ 대규모 트래픽 대비 캐싱
#### ✨ 시군구 데이터 캐싱(담당: 안소나)
- 채워주세요
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>

#### ✨ 평가수 N개 이상 맛집 캐싱(담당: 김연희)
- Redis 와 스케줄러를 이용해서 하루 한 번 새벽 1시에 자동 업로드 기능 구현
- 데이터 동기화를 위해 필드 값 수정이 용이하도록 해시맵으로 변환 후 저장 기능 구현
- 평가가 N개 이상 존재하는 맛집 목록 조회 요청 시 캐싱된 데이터 반환 기능 구현
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>새벽 1시 자동 업로드</strong></div>
        <div>평가수가 몇 시간 단위로 크게 변하지 않을 것 같아 갱신 주기를 하루로 설정하고 서비스 사용량이 비교적 적을 것이라고 예상되는 새벽 1시에 교체작업을 진행하기로 결정했습니다. 목록이 교체되는 시간은 매우 짧을 것이라고 예상되어 Read Through 패턴을 적용하여 DB에서 데이터를 조회해오지는 않게 하였으며 만일 교체되는 시간에 요청이 있을 시 양해 메세지를 반환하도록 구현하였습니다.</div>
        <div><strong>해시맵 저장</strong></div>
        <div>DB와 Redis의 정보 동기화를 위해서는 평가 생성 등의 요청이 생길 때 양쪽 모두 정보를 업데이트해줘야합니다.(Write Through 패턴 적용) 이때 Redis에서는 한 객체의 한 필드만 업데이트하면 되기 때문에 객체가 아닌 해시맵 형태로 저장하는 것이 효율적이라고 판단했습니다.</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/cache/service/RatingCountService.java" target="_blank">RatingCountService</a></br>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/controller/RestaurantController.java" target="_blank">RestaurantController: getRestaurantsByRaitinCount</a></br>
        <a href="https://github.com/wanted-pre-onboarding-backend-team-7/AROUND-MEAL/blob/dev/src/main/java/com/lucky/around/meal/service/RestaurantService.java" target="_blank">RestaurantService: RestaurantService</a></br>
    </div>
</details>

#### ✨ 조회수 N회 이상 맛집 캐싱(담당: 유서정)
- 채워주세요
<details>
    <summary>구현 의도</summary>
    <div>
        <div><strong>제목 1</strong></div>
        <div>내용 1</div>
        <div><strong>제목 2</strong></div>
        <div>내용 2</div>
    </div>
</details>
<details>
    <summary>구현 코드</summary>
    <div>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
        <a href="클래스 주소" target="_blank">클래스 이름</a></br>
    </div>
</details>


</br>

## 5. 트러블 슈팅
👉[캐싱 전략]</br>
👉[데이터 파이프라인 전략]</br>
👉[postgis]</br>
👉[스프링 시큐리티 필터체인 예외처리](https://www.notion.so/b599de75f9594552890cdd68bd0d0841) </br>
👉[JWT 버전차이로 인한 파싱 오류](https://www.notion.so/JWT-200c685081cf4697a50ea810c1b0c614) </br>
