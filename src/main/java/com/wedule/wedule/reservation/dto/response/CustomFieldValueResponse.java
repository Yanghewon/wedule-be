package com.wedule.wedule.reservation.dto.response;

// 예약 조회 응답에서, 커스텀 항목 하나의 라벨과 값을 함께 보여주는 DTO
public class CustomFieldValueResponse {

    private String label;
    private String value;

    public CustomFieldValueResponse(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}