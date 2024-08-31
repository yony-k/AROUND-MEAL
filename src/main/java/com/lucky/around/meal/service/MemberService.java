package com.lucky.around.meal.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.controller.dto.MemberDto;
import com.lucky.around.meal.controller.record.RegisterRecord;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.MemberExceptionType;
import com.lucky.around.meal.exception.exceptionType.RegisterExceptionType;
import com.lucky.around.meal.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JwtService jwtService;

  // 계정명 중복 검증
  public void isExistInDB(RegisterRecord registerRecord) {
    // DB에서 계정명 중복 검증
    boolean isExist = memberRepository.existsByMemberName(registerRecord.memberName());
    // 중복 존재할 시 예외 리턴
    if (isExist) {
      throw new CustomException(RegisterExceptionType.DUPLICATED_MEMBER_NAME);
    }
  }

  // 회원가입
  public void signUp(RegisterRecord registerRecord) {
    // 비밀번호 암호화
    String password = bCryptPasswordEncoder.encode(registerRecord.password());
    // RegisterRecord 를 Member 엔티티로 변환
    Member member = registerRecord.toMember(password);
    // DB 저장
    memberRepository.save(member);
  }

  // 사용자 정보 반환
  public MemberDto getMemberInfo(PrincipalDetails principalDetails) {

    // 인증객체에서 memberId 가져와서 DB에서 Member 객체 가져오기(최신정보)
    Member newMember =
        memberRepository
            .findById(principalDetails.getMemberId())
            .orElseThrow(() -> new CustomException(MemberExceptionType.NOT_FOUND_MEMBER));
    return new MemberDto(newMember);
  }

  // 리프레시 토큰으로 액세스 토큰, 리프레시 토큰 재발급
  public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
    jwtService.reissueRefreshToken(request, response);
  }
}
