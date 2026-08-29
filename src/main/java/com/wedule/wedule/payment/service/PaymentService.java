package com.wedule.wedule.payment.service;

import com.wedule.wedule.payment.dto.request.PaymentCreateRequest;
import com.wedule.wedule.payment.dto.request.PaymentMarkPaidRequest;
import com.wedule.wedule.payment.dto.response.PaymentResponse;
import com.wedule.wedule.payment.dto.response.PaymentSummaryResponse;
import com.wedule.wedule.payment.entity.Payment;
import com.wedule.wedule.payment.repository.PaymentRepository;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    // 결제 항목 등록 (본인 소유 예약인지 검증 포함)
    @Transactional
    public Long createPayment(Long memberId, Long reservationId, PaymentCreateRequest request) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);

        Payment payment = new Payment(reservation, request.getType(), request.getAmount());
        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment.getId();
    }

    // 예약 하나의 결제 현황 요약 조회
    @Transactional(readOnly = true)
    public PaymentSummaryResponse getPaymentSummary(Long memberId, Long reservationId) {
        findOwnedReservation(memberId, reservationId); // 소유권 검증만 하고 결과는 안 씀

        List<Payment> payments = paymentRepository.findByReservationId(reservationId);

        List<PaymentResponse> paymentResponses = payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());

        // 전체 금액 합산 (환불은 음수로 저장되어 있어 자동으로 차감됨)
        int totalAmount = payments.stream()
                .mapToInt(Payment::getAmount)
                .sum();

        // 입금 완료된 것만 걸러서 합산
        int paidAmount = payments.stream()
                .filter(Payment::isPaid)
                .mapToInt(Payment::getAmount)
                .sum();

        int unpaidAmount = totalAmount - paidAmount;
        boolean fullyPaid = unpaidAmount <= 0 && !payments.isEmpty();

        return new PaymentSummaryResponse(paymentResponses, totalAmount, paidAmount, unpaidAmount, fullyPaid);
    }

    // 입금 체크
    @Transactional
    public void markAsPaid(Long memberId, Long reservationId, Long paymentId, PaymentMarkPaidRequest request) {
        Payment payment = findOwnedPayment(memberId, reservationId, paymentId);
        payment.markAsPaid(request.getPaidDate());
    }

    // 입금 취소(되돌리기)
    @Transactional
    public void markAsUnpaid(Long memberId, Long reservationId, Long paymentId) {
        Payment payment = findOwnedPayment(memberId, reservationId, paymentId);
        payment.markAsUnpaid();
    }

    // 결제 항목 삭제
    @Transactional
    public void deletePayment(Long memberId, Long reservationId, Long paymentId) {
        Payment payment = findOwnedPayment(memberId, reservationId, paymentId);
        paymentRepository.delete(payment);
    }

    // Reservation, Package 등에서 썼던 것과 동일한 소유권 검증 패턴
    private Reservation findOwnedReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        return reservation;
    }

    // 결제 항목이 실제로 이 예약, 이 회원의 것이 맞는지까지 검증
    private Payment findOwnedPayment(Long memberId, Long reservationId, Long paymentId) {
        findOwnedReservation(memberId, reservationId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 항목입니다."));

        if (!payment.getReservation().getId().equals(reservationId)) {
            throw new IllegalArgumentException("존재하지 않는 결제 항목입니다.");
        }

        return payment;
    }
}