package com.lucky.around.meal.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

public record RatingRequestDto(
    Long memberId,
    String restaurantId,
    @Range(min = 0, max = 5, message = "0~5 사이의 숫자만 가능합니다.") Integer score,
    @NotBlank(message = "필수 입력값 입니다.")
        @Size(min = 0, max = 255, message = "내용은 최대 255자 이내로 작성해야 합니다.")
        String content) {}
