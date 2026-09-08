package com.wedule.wedule.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;

// 커스텀 항목 등록 요청 DTO
public class CustomFieldCreateRequest {

    @NotBlank(message = "항목 이름은 필수입니다.")
    private String label;

    private int displayOrder;

    public CustomFieldCreateRequest() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
