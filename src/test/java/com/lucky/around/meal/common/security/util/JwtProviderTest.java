package com.lucky.around.meal.common.security.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.common.security.details.PrincipalDetailsService;
import com.lucky.around.meal.common.security.record.JwtRecord;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.entity.enums.MemberRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtProviderTest {

  @InjectMocks private JwtProvider jwtProvider;

  @Mock private PrincipalDetailsService principalDetailsService;

  @BeforeEach
  public void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);
    // cookieProvider 인스턴스 생성
    ReflectionTestUtils.setField(
        jwtProvider, "secretKey", "1EsadfE54865sdf443121CCB134F1D5Es3F322d14241FBBBA7BB");
    ReflectionTestUtils.setField(jwtProvider, "accessTokenHeader", "Authorization");
    ReflectionTestUtils.setField(jwtProvider, "prefix", "Bearer ");
    ReflectionTestUtils.setField(jwtProvider, "accessTokenTTL", 300);
    ReflectionTestUtils.setField(jwtProvider, "refreshTokenTTL", 259200);
    jwtProvider.init();
  }

  @Test
  @DisplayName("엑세스 토큰, 리프레시 토큰 생성 테스트")
  public void generateJwtTest() {
    // given
    // 매개변수 값 임의 생성
    String authorities = MemberRole.USER.name();
    String memberName = "testUser";

    // when
    JwtRecord jwtRecord = jwtProvider.generateJwt(authorities, memberName);

    // then

    // accessToken 값 비교
    // accessToken 클레임 만들기
    Claims accessTokenClaims =
        Jwts.parser()
            .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtProvider, "key"))
            .build()
            .parseSignedClaims(jwtRecord.accessToken())
            .getPayload();

    // accessToken 에 들어있던 memberName 이 일치하는 지 확인
    assertEquals(accessTokenClaims.getSubject(), memberName);

    // refreshToken 값 비교
    // accessToken 클레임 만들기
    Claims refreshTokenClaims =
        Jwts.parser()
            .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtProvider, "key"))
            .build()
            .parseSignedClaims(jwtRecord.refreshToken())
            .getPayload();

    // accessToken 에 들어있던 memberName 이 일치하는 지 확인
    assertEquals(refreshTokenClaims.getSubject(), memberName);
  }

  @Test
  @DisplayName("로그인 성공시 토큰 발행 테스트")
  public void getLoginTokenTest() {
    // given
    String authorities = MemberRole.USER.name();
    String memberName = "testUser";
    List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(authorities));
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(memberName, null, authorityList);

    // when
    JwtRecord jwtRecord = jwtProvider.getLoginToken(authentication);

    // then

    // accessToken 값 비교
    // accessToken 클레임 만들기
    Claims accessTokenClaims =
        Jwts.parser()
            .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtProvider, "key"))
            .build()
            .parseSignedClaims(jwtRecord.accessToken())
            .getPayload();

    // accessToken 에 들어있던 memberName 이 일치하는 지 확인
    assertEquals(accessTokenClaims.getSubject(), memberName);

    // refreshToken 값 비교
    // accessToken 클레임 만들기
    Claims refreshTokenClaims =
        Jwts.parser()
            .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtProvider, "key"))
            .build()
            .parseSignedClaims(jwtRecord.refreshToken())
            .getPayload();

    // accessToken 에 들어있던 memberName 이 일치하는 지 확인
    assertEquals(refreshTokenClaims.getSubject(), memberName);
  }

  @Test
  @DisplayName("재발급 토는 생성 테스트")
  public void getReissueToken() {

    // given
    Member member = Member.builder().role(MemberRole.USER).memberName("testUser").build();

    // when
    JwtRecord jwtRecord = jwtProvider.getReissueToken(member);

    // then

    // accessToken 값 비교
    // accessToken 클레임 만들기
    Claims accessTokenClaims =
        Jwts.parser()
            .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtProvider, "key"))
            .build()
            .parseSignedClaims(jwtRecord.accessToken())
            .getPayload();

    // accessToken 에 들어있던 memberName 이 일치하는 지 확인
    assertEquals(accessTokenClaims.getSubject(), member.getMemberName());

    // refreshToken 값 비교
    // accessToken 클레임 만들기
    Claims refreshTokenClaims =
        Jwts.parser()
            .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtProvider, "key"))
            .build()
            .parseSignedClaims(jwtRecord.refreshToken())
            .getPayload();

    // accessToken 에 들어있던 memberName 이 일치하는 지 확인
    assertEquals(refreshTokenClaims.getSubject(), member.getMemberName());
  }

  @Test
  @DisplayName("인증 객체 생성 테스트")
  public void getAuthenticationTest() {
    // given
    String authorities = "ROLE_USER";
    String memberName = "testUser";

    // 기대하는 인증객체 만들어주기
    List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(authorities));
    Authentication expectedAuth =
        new UsernamePasswordAuthenticationToken(memberName, null, authorityList);

    // 메소드 내에 사용되는 메소드의 매개변수로 넣어줄 Member 객체
    Member member = Member.builder().role(MemberRole.USER).memberName(memberName).build();

    // DB에서 사용자 찾아오는 부분
    PrincipalDetails principalDetails = new PrincipalDetails(member);
    when(principalDetailsService.loadUserByUsername(memberName)).thenReturn(principalDetails);

    // 메소드 매개변수로 넣어줄 accessToken 생성
    JwtRecord jwtRecord = jwtProvider.generateJwt(authorities, memberName);

    // when
    Authentication actualAuth = jwtProvider.getAuthentication("Bearer " + jwtRecord.accessToken());

    // then
    // 기대했던 인증객체와 비교
    assertEquals(expectedAuth.getName(), actualAuth.getName());
    assertEquals(expectedAuth.getAuthorities(), actualAuth.getAuthorities());
  }
}
