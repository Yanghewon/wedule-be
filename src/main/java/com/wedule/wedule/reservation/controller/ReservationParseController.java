package com.wedule.wedule.reservation.controller;

import com.wedule.wedule.reservation.repository.CustomFieldRepository;
import com.wedule.wedule.reservation.service.ReservationAiParsingService;
import com.wedule.wedule.reservation.dto.request.ReservationParseRequest;
import com.wedule.wedule.reservation.dto.response.ReservationParseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 예약 양식 텍스트 자동 파싱 API
@RestController
@RequestMapping("/api/reservations/parse")
public class ReservationParseController {

    private final ReservationAiParsingService reservationAiParsingService;
    private final CustomFieldRepository customFieldRepository;

    public ReservationParseController(ReservationAiParsingService reservationAiParsingService,
                                      CustomFieldRepository customFieldRepository) {
        this.reservationAiParsingService = reservationAiParsingService;
        this.customFieldRepository = customFieldRepository;
    }

    // POST /api/reservations/parse
    // 텍스트를 분석만 하고, 실제 예약 저장은 하지 않음 (미리보기 용도)
    @PostMapping
    public ResponseEntity<ReservationParseResponse> parse(
            Authentication authentication,
            @RequestBody ReservationParseRequest request) {
        Long memberId = (Long) authentication.getPrincipal();
        var customFields = customFieldRepository.findByMemberIdOrderByDisplayOrderAsc(memberId);
        ReservationParseResponse response = reservationAiParsingService.parse(request.getRawText(), customFields);
        return ResponseEntity.ok(response);
    }
}