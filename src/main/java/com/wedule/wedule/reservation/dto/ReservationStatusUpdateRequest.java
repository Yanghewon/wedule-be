package com.wedule.wedule.reservation.dto;

import com.wedule.wedule.reservation.ReservationStatus;

// 예약 상태 변경 요청 DTO
public class ReservationStatusUpdateRequest {

    private ReservationStatus status;

    public ReservationStatusUpdateRequest() {
        this.status = status;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}