package com.lucky.around.meal.service;

import org.springframework.stereotype.Service;

import com.lucky.around.meal.controller.record.RegisterRecord;
import com.lucky.around.meal.entity.Member;
import com.lucky.around.meal.exception.CustomException;
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
}
