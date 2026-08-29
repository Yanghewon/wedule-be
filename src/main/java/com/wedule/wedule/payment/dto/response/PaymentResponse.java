package com.wedule.wedule.payment.dto.response;

import com.wedule.wedule.payment.entity.Payment;
import com.wedule.wedule.payment.entity.PaymentType;

import java.time.LocalDate;

// Payment 엔티티를, 클라이언트에게 보여줄 형태로 변환하는 그릇
public class PaymentResponse {

    private Long id;
    private PaymentType type;
    private int amount;
    private boolean isPaid;
    private LocalDate paidDate;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.type = payment.getType();
        this.amount = payment.getAmount();
        this.isPaid = payment.isPaid();
        this.paidDate = payment.getPaidDate();
    }

    public Long getId() {
        return id;
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