package com.wedule.wedule.option.dto;

// 옵션 생성 성공 응답
public class OptionCreateResponse {

    private Long id;
    private String message;

    public OptionCreateResponse(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}