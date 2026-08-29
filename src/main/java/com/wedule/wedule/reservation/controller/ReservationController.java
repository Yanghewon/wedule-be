package com.wedule.wedule.reservation.controller;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.reservation.service.ReservationService;
import com.wedule.wedule.reservation.dto.request.ReservationCreateRequest;
import com.wedule.wedule.reservation.dto.response.ReservationCreateResponse;
import com.wedule.wedule.reservation.dto.response.ReservationResponse;
import com.wedule.wedule.reservation.dto.request.ReservationStatusUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // POST /api/reservations
    // Authentication 파라미터: Spring Security가 자동으로 "지금 이 요청을 보낸 인증된 사용자 정보"를 넣어줌
    // (JwtAuthenticationFilter가 SecurityContextHolder에 등록해둔 바로 그 정보)
    @PostMapping
    public ResponseEntity<ReservationCreateResponse> createReservation(
            Authentication authentication,
            @RequestBody ReservationCreateRequest request
    ) {
        // getPrincipal(): 인증 필터에서 등록할 때 넣었던 첫 번째 값(memberId)을 꺼냄
        Long memberId = (Long) authentication.getPrincipal();

        Long reservationId = reservationService.createReservation(memberId, request);
        return ResponseEntity.ok(new ReservationCreateResponse(reservationId, "예약이 완료되었습니다🤍"));
    }

    // GET /api/reservations — 로그인한 회원의 예약 목록 조회
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(reservationService.getReservations(memberId));
    }

    // GET /api/reservations/{id} — 예약 단건 조회
// @PathVariable: URL 경로에 포함된 {id} 값을 그대로 파라미터로 받음
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(reservationService.getReservation(memberId, id));
    }

    // PATCH /api/reservations/{id}/status — 예약 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateStatus(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody ReservationStatusUpdateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        reservationService.updateStatus(memberId, id, request);
        return ResponseEntity.ok(new MessageResponse("예약 상태가 변경되었습니다🤍"));
    }

    // DELETE /api/reservations/{id} — 예약 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReservation(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        reservationService.deleteReservation(memberId, id);
        return ResponseEntity.ok(new MessageResponse("예약이 삭제되었습니다."));
    }
}