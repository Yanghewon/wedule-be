package com.wedule.wedule.reservation.controller;

import com.wedule.wedule.reservation.dto.request.ReservationParseRequest;
import com.wedule.wedule.reservation.dto.response.ReservationParseResponse;
import com.wedule.wedule.reservation.service.ReservationAiParsingService;
import com.wedule.wedule.reservation.service.ReservationParsingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 예약 양식 텍스트 자동 파싱 API
@RestController
@RequestMapping("/api/reservations/parse")
public class ReservationParseController {

    private final ReservationAiParsingService reservationAiParsingService;

    public ReservationParseController(ReservationAiParsingService reservationAiParsingService) {
        this.reservationAiParsingService = reservationAiParsingService;
    }

    // POST /api/reservations/parse
    // 텍스트를 분석만 하고, 실제 예약 저장은 하지 않음 (미리보기 용도)
    @PostMapping
    public ResponseEntity<ReservationParseResponse> parse(@RequestBody ReservationParseRequest request) {
        ReservationParseResponse response = reservationAiParsingService.parse(request.getRawText());
        return ResponseEntity.ok(response);
    }
}