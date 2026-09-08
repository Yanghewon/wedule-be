package com.wedule.wedule.payment.dto.request;

import com.wedule.wedule.payment.entity.PaymentType;
import jakarta.validation.constraints.NotNull;

// 결제 항목 등록 요청 DTO
public class PaymentCreateRequest {

    @NotNull(message = "결제 종류를 선택해주세요.")
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