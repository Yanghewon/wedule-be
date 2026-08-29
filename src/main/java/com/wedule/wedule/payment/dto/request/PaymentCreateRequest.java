package com.wedule.wedule.payment.dto.request;

import com.wedule.wedule.payment.entity.PaymentType;

// 결제 항목 등록 요청 DTO
public class PaymentCreateRequest {

    private PaymentType type;
    private int amount;

    public PaymentCreateRequest() {
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}