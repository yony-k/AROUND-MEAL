package com.lucky.around.meal.service;

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
    // RegisterRecord 를 Member 엔티티로 변환
    Member member = registerRecord.toMember();
    // DB 저장
    memberRepository.save(member);
  }

  // 사용자 정보 반환
  public MemberDto getMemberInfo(PrincipalDetails principalDetails) {

    // 인증객체에서 Member 객체 가져오기
    Member loginMember = principalDetails.getMember();

    // 인증객체 이용해서 DB에서 Member 객체 가져오기(최신정보)
    Member newMember =
        memberRepository
            .findById(loginMember.getMemberId())
            .orElseThrow(() -> new CustomException(MemberExceptionType.NOT_FOUND_MEMBER));
    return new MemberDto(newMember);
  }
}
