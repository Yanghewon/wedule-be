package com.wedule.wedule.reservation.dto.request;

import com.wedule.wedule.reservation.dto.ReservationStatus;

// 예약 상태 변경 요청 DTO
public class ReservationStatusUpdateRequest {

    private ReservationStatus status;

    // Jackson이 JSON -> 객체 변환 시 필요로 하는 기본 생성자
    public ReservationStatusUpdateRequest() {
    }

    public ReservationStatus getStatus() {
        return status;
    }

    // Jackson이 이 메서드를 통해 JSON의 "status" 값을 채워 넣음
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}