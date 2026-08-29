package com.wedule.wedule.payment.dto.response;
import java.util.List;

// 예약 하나의 결제 현황 요약
// 개별 결제 항목 목록 + 합산된 금액 정보를 함께 제공
public class PaymentSummaryResponse {

    private List<PaymentResponse> payments;
    private int totalAmount;      // 전체 결제 항목 금액의 합 (환불은 음수라 자동으로 차감됨)
    private int paidAmount;       // 입금 완료된 금액의 합
    private int unpaidAmount;     // 아직 입금 안 된 금액의 합 (미수금)
    private boolean fullyPaid;    // 완납 여부 (미수금이 0이면 true)

    public PaymentSummaryResponse(List<PaymentResponse> payments, int totalAmount,
                                  int paidAmount, int unpaidAmount, boolean fullyPaid) {
        this.payments = payments;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.unpaidAmount = unpaidAmount;
        this.fullyPaid = fullyPaid;
    }

    public List<PaymentResponse> getPayments() {
        return payments;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public int getUnpaidAmount() {
        return unpaidAmount;
    }

    public boolean isFullyPaid() {
        return fullyPaid;
    }
}