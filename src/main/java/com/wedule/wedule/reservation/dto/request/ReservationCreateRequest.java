package com.wedule.wedule.reservation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 예약 생성 요청 DTO
// 클라이언트가 { "groomName": "...", "weddingDate": "2026-08-09", ... } 형태로 보내면
// Jackson이 이 필드들에 자동으로 값을 채워줌
public class ReservationCreateRequest {

    private Long packageId;
    private String groomName;
    private String brideName;
    private String phone;
    private LocalDate weddingDate;
    private LocalTime weddingTime;
    private String venueName;
    private List<Long> optionIds;

    // Jackson이 JSON -> 객체 변환 시 필요로 하는 기본 생성자
    public ReservationCreateRequest() {
    }

    public ReservationCreateRequest(String groomName, String brideName, String phone,
                                    LocalDate weddingDate, LocalTime weddingTime, String venueName) {
        this.groomName = groomName;
        this.brideName = brideName;
        this.phone = phone;
        this.weddingDate = weddingDate;
        this.weddingTime = weddingTime;
        this.venueName = venueName;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getGroomName() {
        return groomName;
    }

    public String getBrideName() {
        return brideName;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public LocalTime getWeddingTime() {
        return weddingTime;
    }

    public String getVenueName() {
        return venueName;
    }

    public List<Long> getOptionIds() {
        return optionIds;
    }

    public void setOptionIds(List<Long> optionIds) {
        this.optionIds = optionIds;
    }
}