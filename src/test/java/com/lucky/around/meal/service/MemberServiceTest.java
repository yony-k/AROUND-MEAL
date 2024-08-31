package com.lucky.around.meal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.controller.dto.MemberDto;
import com.lucky.around.meal.controller.record.RegisterRecord;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.repository.MemberRepository;

public class MemberServiceTest {
  @Mock private MemberRepository memberRepository;
  @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

  @InjectMocks private MemberService memberService;

  // 회원가입시 사용될 RegisterRecord
  private RegisterRecord registerRecord;
  // 회원가입시 사용될 암호화 비밀번호
  private String encodedPassword = "encryptedPassword";
  private Member member;

  @BeforeEach
  void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);

    // 사용자 정보
    String memberName = "sungmin11";
    String password = "123456";
    String email = "sungmin11@gmail.com";

    // RegisterRecord 초기화
    registerRecord = new RegisterRecord(memberName, password, email);

    // Member 초기화
    member = Member.builder().memberName(memberName).password(encodedPassword).email(email).build();
  }

  @Test
  @DisplayName("계정 중복 테스트 성공 케이스")
  public void isExistInDBSuccessTest() {
    // given
    // DB에 중복체크할 때 false(중복 없음) 반환
    when(memberRepository.existsByMemberName(registerRecord.memberName())).thenReturn(false);

    // when & then
    // 예외가 발생하지 않는지 확인
    assertDoesNotThrow(
        () -> {
          memberService.isExistInDB(registerRecord);
        });
  }

  @Test
  @DisplayName("계정 중복 테스트 실패 케이스")
  public void isExistInDBFailureTest() {
    // given
    // DB에 중복체크할 때 false(중복 있음) 반환
    when(memberRepository.existsByMemberName(registerRecord.memberName())).thenReturn(true);

    // when & then
    // 예외가 발생하는지 확인
    assertThrows(
        CustomException.class,
        () -> {
          memberService.isExistInDB(registerRecord);
        });
  }

  @Test
  @DisplayName("회원가입 테스트")
  public void signUpTest() {
    // given
    // 비밀번호 암호화 리턴값 지정
    when(bCryptPasswordEncoder.encode(registerRecord.password())).thenReturn(encodedPassword);

    // when
    memberService.signUp(registerRecord);

    // then
    /*
    메소드 안에서 memberRepository.save를 했을 때
    사용된 Member 객체 파라미터가
    내가 사용될 거라고 생각한 Member 객체와 일치하는지 확인
    */
    verify(memberRepository)
        .save(
            argThat(
                m ->
                    m.getMemberName().equals(member.getMemberName())
                        && m.getPassword().equals(member.getPassword())
                        && m.getEmail().equals(member.getEmail())));
  }

  @Test
  @DisplayName("사용자 정보 반환 성공 케이스")
  public void getMemberSuccessInfo() {
    // given
    // 매개변수로 넣어줄 인증객체 생성
    PrincipalDetails principalDetails = new PrincipalDetails(member);
    // 위 인증객체를 이용해 메소드를 호출했을 때 제대로 된 Member 객체 반환
    when(memberRepository.findById(principalDetails.getMemberId())).thenReturn(Optional.of(member));

    // when
    MemberDto memberDto = memberService.getMemberInfo(principalDetails);

    // then
    // MemberDto 가 제대로 만들어졌는지 확인
    assertNotNull(memberDto);
    // MemberDto의 값이 내가 반환 지정한 Member 객체와 동일한지 확인
    assertEquals(member.getMemberName(), memberDto.getMemberName());
    assertEquals(member.getEmail(), memberDto.getEmail());
  }

  @Test
  @DisplayName("사용자 정보 반환 실패 케이스")
  public void getMemberFailureInfo() {
    // given
    PrincipalDetails principalDetails = new PrincipalDetails(member);
    // DB에서 Member 객체를 받아오지 못했을 떄
    when(memberRepository.findById(principalDetails.getMemberId())).thenReturn(Optional.empty());

    // when & then
    // 예외가 발생하는지 확인
    assertThrows(
        CustomException.class,
        () -> {
          memberService.getMemberInfo(principalDetails);
        });
  }
}
