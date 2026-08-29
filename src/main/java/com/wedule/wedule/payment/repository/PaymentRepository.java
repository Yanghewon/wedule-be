package com.wedule.wedule.payment.repository;

import com.wedule.wedule.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 특정 예약의 결제 항목 전체 조회
    List<Payment> findByReservationId(Long reservationId);
}
