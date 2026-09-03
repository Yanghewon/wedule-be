package com.wedule.wedule.contract.dto.response;

import com.wedule.wedule.contract.entity.ContractTemplate;

public class ContractTemplateResponse {

    private Long id;
    private String content;

    public ContractTemplateResponse(ContractTemplate template) {
        this.id = template.getId();
        this.content = template.getContent();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
