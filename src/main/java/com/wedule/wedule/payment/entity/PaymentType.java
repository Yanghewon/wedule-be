package com.wedule.wedule.payment.entity;

// 결제 항목의 종류
public enum PaymentType {
    // 계약금
    DEPOSIT,

    // 잔금
    BALANCE,

    // 추가금
    ADDITIONAL,

    // 환불 (음수 금액으로 저장해서 나중에 합산 계산 시 부호만으로 자동 반영되게 함)
    REFUND
}