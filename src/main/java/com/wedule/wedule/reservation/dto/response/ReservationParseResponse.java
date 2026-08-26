package com.wedule.wedule.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

// 예약 양식 텍스트 파싱 결과를 담는 DTO
// 실제로 저장된 데이터가 아니라 "미리보기" 값이므로,
// 파싱에 실패한 필드는 null(빈 값)로 남아있을 수 있음
// -> 사용자가 화면에서 직접 채우거나 수정한 뒤, 별도로 예약 생성 API를 호출해야 함
public class ReservationParseResponse {

    private String groomName;
    private String brideName;
    private String phone;
    private LocalDate weddingDate;
    private LocalTime weddingTime;
    private String venueName;

    public ReservationParseResponse() {
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(LocalDate weddingDate) {
        this.weddingDate = weddingDate;
    }

    public LocalTime getWeddingTime() {
        return weddingTime;
    }

    public void setWeddingTime(LocalTime weddingTime) {
        this.weddingTime = weddingTime;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
}