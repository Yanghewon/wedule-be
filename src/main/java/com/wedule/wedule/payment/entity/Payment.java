package com.wedule.wedule.payment.entity;

import com.wedule.wedule.reservation.entity.Reservation;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// 예약에 대한 결제 항목 (계약금, 잔금, 추가금, 환불)
// 한 예약에 여러 개의 결제 항목이 딸릴 수 있음 (1:N)
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id, nullable = false")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    // 결제 금액 (환불은 음수로 저장)
    @Column(nullable = false)
    private int amount;

    // 입금 여부 - 이 값들을 모아서 "완납 여부"를 계산하게 됨
    @Column(nullable = false)
    private boolean isPaid;

    // 실제 입금된 날짜 (아직 입금 안 됐으면 null)
    private LocalDate paidDate;

    protected Payment() {
    }

    // 생성 시점에는 아직 입금 전이라고 가정 (isPaid = false로 고정)
    public Payment(Reservation reservation, PaymentType type, int amount) {
        this.reservation = reservation;
        this.type = type;
        this.amount = amount;
        this.isPaid = false;
    }

    // 입금 체크 처리 - 입금 여부를 true로 바꾸면서 입금 날짜도 함께 기록
    public void markAsPaid(LocalDate paidDate) {
        this.isPaid = true;
        this.paidDate = paidDate;
    }

    // 입금 취소(실수로 체크한 경우 되돌리기)
    public void markAsUnpaid() {
        this.isPaid = false;
        this.paidDate = null;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public PaymentType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }
}