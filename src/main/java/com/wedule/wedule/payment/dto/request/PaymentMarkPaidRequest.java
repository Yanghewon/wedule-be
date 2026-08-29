package com.wedule.wedule.payment.dto.request;

import java.time.LocalDate;

// 입금 체크 요청 DTO
public class PaymentMarkPaidRequest {

    private LocalDate paidDate;

    public PaymentMarkPaidRequest() {
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
}