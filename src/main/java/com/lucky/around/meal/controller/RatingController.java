package com.lucky.around.meal.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucky.around.meal.controller.request.RatingRequestDto;
import com.lucky.around.meal.controller.response.RatingResponseDto;
import com.lucky.around.meal.service.RatingService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/rating")
@RestController
@RequiredArgsConstructor
public class RatingController {

  private final RatingService ratingService;

  @PostMapping
  public ResponseEntity<RatingResponseDto> createRating(
      @RequestBody @Valid RatingRequestDto requestDto) {
    RatingResponseDto responseDto = ratingService.createRating(requestDto);
    return ResponseEntity.ok(responseDto);
  }
}
