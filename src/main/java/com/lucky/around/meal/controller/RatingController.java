package com.lucky.around.meal.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.common.security.details.PrincipalDetails;
import com.lucky.around.meal.controller.request.RatingRequestDto;
import com.lucky.around.meal.controller.response.RatingResponseDto;
import com.lucky.around.meal.exception.CustomException;
import com.lucky.around.meal.exception.exceptionType.SecurityExceptionType;
import com.lucky.around.meal.service.RatingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/rating")
@RestController
@RequiredArgsConstructor
public class RatingController {

  private final RatingService ratingService;

  @PostMapping
  public ResponseEntity<RatingResponseDto> createRating(
      @RequestBody @Valid RatingRequestDto requestDto,
      @AuthenticationPrincipal PrincipalDetails principalDetails) {

    if (principalDetails == null) {
      throw new CustomException(SecurityExceptionType.REQUIRED_AUTHENTICATION);
    }

    Long memberId = principalDetails.getMemberId();
    log.info("로그인 멤버 id : " + memberId);
    RatingResponseDto responseDto = ratingService.createRating(memberId, requestDto);
    return ResponseEntity.ok(responseDto);
  }
}
