package com.lucky.around.meal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.geo.Point;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lucky.around.meal.common.redis.RedisRepository;
import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.controller.request.*;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.repository.MemberRepository;

class LocationServiceTest {

  private static final Long MEMBER_ID = 1L;
  private static final Point EXPECTED_POINT = new Point(126.9780, 37.5665);
  private static final LocationResponseDto EXPECTED_DTO =
      new LocationResponseDto(126.9780, 37.5665);
  private static final StaticLocationRequestDto STATIC_LOCATION_REQUEST =
      new StaticLocationRequestDto(126.9780, 37.5665);
  private static final StaticLocationResponseDto STATIC_LOCATION_RESPONSE =
      new StaticLocationResponseDto(126.9780, 37.5665);

  @Mock private RedisRepository redis;
  @Mock private MemberRepository memberRepository;
  @Mock private Member member;

  @InjectMocks private LocationService locationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    setUpAuthentication();
  }

  private void setUpAuthentication() {
    Authentication authentication = mock(Authentication.class);
    PrincipalDetails principal = mock(PrincipalDetails.class);
    when(principal.getMemberId()).thenReturn(MEMBER_ID);
    when(authentication.getPrincipal()).thenReturn(principal);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("회원 실시간 위치 정보 Redis 저장")
  void saveMemberLocation() {
    // Given
    MemberLocationRequestDto request = new MemberLocationRequestDto(126.9780, 37.5665);
    String key = generateRedisKey(MEMBER_ID);

    // When
    locationService.saveMemberLocation(request);

    // Then
    verify(redis).saveRealTimeLocation(key, request.lon(), request.lat(), MEMBER_ID);
  }

  @Test
  @DisplayName("회원 실시간 위치 정보 Redis 조회")
  void getMemberLocation() {
    // Given
    String key = generateRedisKey(MEMBER_ID);
    when(redis.getRealTimeLocation(key, MEMBER_ID)).thenReturn(EXPECTED_POINT);

    // When
    Point actualPoint = locationService.getMemberLocation();

    // Then
    assertPointEquals(EXPECTED_POINT, actualPoint);
  }

  @Test
  @DisplayName("회원 실시간 위치 정보 DTO 변환")
  void getMemberLocationToTrans() {
    // Given
    String key = generateRedisKey(MEMBER_ID);
    when(redis.getRealTimeLocation(key, MEMBER_ID)).thenReturn(EXPECTED_POINT);

    // When
    LocationResponseDto actualDto = locationService.getMemberLocationToTrans();

    // Then
    assertLocationResponseDtoEquals(EXPECTED_DTO, actualDto);
  }

  @Test
  @DisplayName("회원 정적인 위치 정보 업데이트")
  void updateStaticLocation() {
    // Given
    when(memberRepository.findById(MEMBER_ID)).thenReturn(java.util.Optional.of(member));

    // When
    locationService.updateStaticLocation(STATIC_LOCATION_REQUEST);

    // Then
    verify(member).updateLocation(STATIC_LOCATION_REQUEST.lon(), STATIC_LOCATION_REQUEST.lat());
  }

  @Test
  @DisplayName("회원 정적인 위치 정보 조회")
  void getStaticLocation() {
    // Given
    when(memberRepository.findById(MEMBER_ID)).thenReturn(java.util.Optional.of(member));
    when(member.getLon()).thenReturn(STATIC_LOCATION_RESPONSE.lon());
    when(member.getLat()).thenReturn(STATIC_LOCATION_RESPONSE.lat());

    // When
    StaticLocationResponseDto actualDto = locationService.getStaticLocation();

    // Then
    assertStaticLocationResponseDtoEquals(STATIC_LOCATION_RESPONSE, actualDto);
  }

  private String generateRedisKey(Long memberId) {
    return "location:" + memberId;
  }

  private void assertPointEquals(Point expected, Point actual) {
    assertNotNull(actual, "Redis에서 위치 정보를 가져올 수 없습니다.");
    assertEquals(expected.getX(), actual.getX(), "X좌표가 일치하지 않습니다.");
    assertEquals(expected.getY(), actual.getY(), "Y좌표가 일치하지 않습니다.");
  }

  private void assertLocationResponseDtoEquals(
      LocationResponseDto expected, LocationResponseDto actual) {
    assertNotNull(actual, "DTO 변환에 실패하였습니다.");
    assertEquals(expected.lat(), actual.lat(), "DTO의 lat 값이 일치하지 않습니다.");
    assertEquals(expected.lon(), actual.lon(), "DTO의 lon 값이 일치하지 않습니다.");
  }

  private void assertStaticLocationResponseDtoEquals(
      StaticLocationResponseDto expected, StaticLocationResponseDto actual) {
    assertNotNull(actual, "정적 위치 정보 조회에 실패하였습니다.");
    assertEquals(expected.lat(), actual.lat(), "DTO의 lat 값이 일치하지 않습니다.");
    assertEquals(expected.lon(), actual.lon(), "DTO의 lon 값이 일치하지 않습니다.");
  }
}
