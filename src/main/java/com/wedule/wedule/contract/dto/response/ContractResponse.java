package com.wedule.wedule.contract.dto.response;

import com.wedule.wedule.contract.entity.Contract;
import com.wedule.wedule.contract.entity.ContractStyle;

import java.time.LocalDateTime;

public class ContractResponse {

    private Long id;
    private ContractStyle style;
    private String content;
    private LocalDateTime createdAt;

    public ContractResponse(Contract contract) {
        this.id = contract.getId();
        this.style = contract.getStyle();
        this.content = contract.getContent();
        this.createdAt = contract.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public ContractStyle getStyle() {
        return style;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}