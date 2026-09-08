package com.wedule.wedule.reservation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import com.wedule.wedule.reservation.dto.request.CustomFieldValueRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// 예약 생성 요청 DTO
// 클라이언트가 { "groomName": "...", "weddingDate": "2026-08-09", ... } 형태로 보내면
// Jackson이 이 필드들에 자동으로 값을 채워줌
public class ReservationCreateRequest {

    @NotNull(message = "촬영 패키지를 선택해주세요.")
    private Long packageId;

    @NotBlank(message = "신랑 성함은 필수입니다.")
    private String groomName;

    @NotBlank(message = "신부 성함은 필수입니다.")
    private String brideName;

    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;

    @NotNull(message = "예식 날짜는 필수입니다.")
    @Future(message = "예식 날짜는 오늘 이후여야 합니다.")
    private LocalDate weddingDate;

    @NotNull(message = "예식 시간은 필수입니다.")
    private LocalTime weddingTime;

    @NotBlank(message = "예식 장소는 필수입니다.")
    private String venueName;
    private List<Long> optionIds;
    private List<CustomFieldValueRequest> customFieldValues;

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

    public List<CustomFieldValueRequest> getCustomFieldValues() {
        return customFieldValues;
    }

    public void setCustomFieldValues(List<CustomFieldValueRequest> customFieldValues) {
        this.customFieldValues = customFieldValues;
    }

}