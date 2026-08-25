package com.wedule.wedule.reservation.dto.response;

import com.wedule.wedule.reservation.entity.CustomField;

// 커스텀 항목 조회 응답 DTO
public class CustomFieldResponse {

    private Long id;
    private String label;
    private int displayOrder;

    public CustomFieldResponse(CustomField customField) {
        this.id = customField.getId();
        this.label = customField.getLabel();
        this.displayOrder = customField.getDisplayOrder();
    }
    public Long getId() {
        return id;
    }
    public String getLabel() {
        return label;
    }
    public int getDisplayOrder() {
        return displayOrder;
    }
}
