package com.lucky.around.meal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lucky.around.meal.controller.request.*;
import com.lucky.around.meal.controller.response.*;
import com.lucky.around.meal.service.*;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/locations")
@RestController
@RequiredArgsConstructor
public class LocationController {

  private final LocationService service;

  @PostMapping("/real-time")
  public ResponseEntity<Void> saveMemberLocation(@RequestBody MemberLocationRequestDto request) {
    service.saveMemberLocation(request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/real-time")
  public ResponseEntity<LocationResponseDto> getMemberLocation() {
    LocationResponseDto location = service.getMemberLocationToTrans();
    return ResponseEntity.ok(location);
  }
}
