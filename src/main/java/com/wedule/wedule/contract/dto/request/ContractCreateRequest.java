package com.wedule.wedule.contract.dto.request;

import com.wedule.wedule.contract.entity.ContractStyle;
import jakarta.validation.constraints.NotNull;

// 계약서 생성/수정 요청
// content를 비워서 보내면, 작가가 등록해둔 기본 템플릿 내용을 그대로 사용함
public class ContractCreateRequest {

    @NotNull(message = "계약서 스타일을 선택해주세요.")
    private ContractStyle style;

    private String content;

    public ContractCreateRequest() {
    }

    public ContractStyle getStyle() {
        return style;
    }

    public void setStyle(ContractStyle style) {
        this.style = style;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}