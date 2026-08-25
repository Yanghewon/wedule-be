package com.wedule.wedule.reservation.dto.request;

// 예약 생성/수정 시, 커스텀 항목 하나에 대한 값을 담는 DTO
// 예: { "customFieldId": 1, "value": "12시" }
public class CustomFieldValueRequest {

    private Long customFieldId;
    private String value;

    public CustomFieldValueRequest() {
    }

    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}