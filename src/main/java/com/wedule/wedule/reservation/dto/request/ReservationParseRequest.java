package com.wedule.wedule.reservation.dto.request;

// 예약 양식 텍스트를 통째로 받는 요청 DTO
// 클라이언트가 { "rawText": "신랑/신부님 성함 : ...\n연락처 : ..." } 형태로 보냄
public class ReservationParseRequest {

    private String rawText;

    public ReservationParseRequest() {
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
}