package com.wedule.wedule.payment.controller;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.payment.dto.request.PaymentCreateRequest;
import com.wedule.wedule.payment.dto.request.PaymentMarkPaidRequest;
import com.wedule.wedule.payment.dto.response.PaymentSummaryResponse;
import com.wedule.wedule.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// 예약에 딸린 결제 항목을 관리하는 API
// 경로에 reservationId를 포함해서, "어느 예약의 결제인지"를 URL만 봐도 알 수 있게 함
@RestController
@RequestMapping("/api/reservations/{reservationId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // POST /api/reservations/{reservationId}/payments — 결제 항목 등록
    @PostMapping
    public ResponseEntity<MessageResponse> createPayment(
            Authentication authentication,
            @PathVariable Long reservationId,
            @RequestBody PaymentCreateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        paymentService.createPayment(memberId, reservationId, request);
        return ResponseEntity.ok(new MessageResponse("결제 항목이 등록되었습니다🤍"));
    }

    // GET /api/reservations/{reservationId}/payments — 결제 현황 요약 조회
    @GetMapping
    public ResponseEntity<PaymentSummaryResponse> getPaymentSummary(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.getPaymentSummary(memberId, reservationId));
    }

    // PATCH /api/reservations/{reservationId}/payments/{paymentId}/paid — 입금 체크
    @PatchMapping("/{paymentId}/paid")
    public ResponseEntity<MessageResponse> markAsPaid(
            Authentication authentication,
            @PathVariable Long reservationId,
            @PathVariable Long paymentId,
            @RequestBody PaymentMarkPaidRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        paymentService.markAsPaid(memberId, reservationId, paymentId, request);
        return ResponseEntity.ok(new MessageResponse("입금 처리되었습니다🤍"));
    }

    // PATCH /api/reservations/{reservationId}/payments/{paymentId}/unpaid — 입금 취소
    @PatchMapping("/{paymentId}/unpaid")
    public ResponseEntity<MessageResponse> markAsUnpaid(
            Authentication authentication,
            @PathVariable Long reservationId,
            @PathVariable Long paymentId
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        paymentService.markAsUnpaid(memberId, reservationId, paymentId);
        return ResponseEntity.ok(new MessageResponse("입금이 취소되었습니다."));
    }

    // DELETE /api/reservations/{reservationId}/payments/{paymentId} — 결제 항목 삭제
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<MessageResponse> deletePayment(
            Authentication authentication,
            @PathVariable Long reservationId,
            @PathVariable Long paymentId
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        paymentService.deletePayment(memberId, reservationId, paymentId);
        return ResponseEntity.ok(new MessageResponse("결제 항목이 삭제되었습니다."));
    }
}