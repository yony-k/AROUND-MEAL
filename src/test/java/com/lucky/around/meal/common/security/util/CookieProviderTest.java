package com.lucky.around.meal.common.security.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.lucky.around.meal.exception.CustomException;

public class CookieProviderTest {

  @InjectMocks private CookieProvider cookieProvider;

  @Mock private HttpServletRequest request;

  private String refreshToekn;

  @BeforeEach
  public void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);
    // cookieProvider 인스턴스 생성
    ReflectionTestUtils.setField(cookieProvider, "cookieName", "testCookieName");
    ReflectionTestUtils.setField(cookieProvider, "cookieLimitTime", 3600);
    ReflectionTestUtils.setField(cookieProvider, "cookieDomain", "example.com");
    ReflectionTestUtils.setField(cookieProvider, "cookieHttpOnly", true);
    refreshToekn = "testToken";
  }

  @Test
  @DisplayName("쿠키 생성 테스트")
  public void createRefreshTokenCookieTest() {
    // given

    // when
    Cookie result = cookieProvider.createRefreshTokenCookie(refreshToekn);

    // then
    assertNotNull(result);
    assertEquals("testCookieName", result.getName());
    assertEquals(refreshToekn, result.getValue());
    assertEquals(3600, result.getMaxAge());
    assertEquals("example.com", result.getDomain());
    assertTrue(result.isHttpOnly());
  }

  @Test
  @DisplayName("리프레시 토큰 가져오기 성공 케이스")
  public void getRefreshTokenCookieSuccessTest() {
    // given
    Cookie cookie = new Cookie("testCookieName", refreshToekn);
    Cookie[] cookies = {cookie};
    when(request.getCookies()).thenReturn(cookies);

    // when
    Optional<Cookie> findCookie = cookieProvider.getRefreshTokenCookie(request);

    // then
    assertTrue(findCookie.isPresent());
    assertEquals(refreshToekn, findCookie.get().getValue());
  }

  @Test
  @DisplayName("리프레시 토큰 가져오기 실패 케이스")
  public void getRefreshTokenCookieFailureTest() {
    // given
    when(request.getCookies()).thenReturn(new Cookie[0]);

    // when
    Optional<Cookie> findCookie = cookieProvider.getRefreshTokenCookie(request);

    // then
    assertFalse(findCookie.isPresent());
  }

  @Test
  @DisplayName("리프레시 토큰 삭제 성공 케이스")
  public void deleteRefreshTokenCookieSuccessTest() {
    // given
    Cookie cookie = new Cookie("testCookieName", refreshToekn);
    Cookie[] cookies = {cookie};
    when(request.getCookies()).thenReturn(cookies);

    // when
    Cookie result = cookieProvider.deleteRefreshTokenCookie(request);

    // then
    assertEquals(result.getMaxAge(), 0);
  }

  @Test
  @DisplayName("리프레시 토큰 삭제 실패 케이스")
  public void deleteRefreshTokenCookieFailureTest() {
    // given
    when(request.getCookies()).thenReturn(new Cookie[0]);

    // when & then
    assertThrows(CustomException.class, () -> cookieProvider.deleteRefreshTokenCookie(request));
  }
}
