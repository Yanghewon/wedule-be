package com.wedule.wedule.reservation.dto;

// 예약 생성 성공 응답
public class ReservationCreateResponse {

    private Long id;
    private String message;

    public ReservationCreateResponse(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}