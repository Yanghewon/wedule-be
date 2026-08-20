package com.wedule.wedule.common.dto;

// 별도로 돌려줄 데이터는 없고, 단순히 "성공했다"는 메시지만 응답할 때 재사용하는 공통 DTO
// 특정 도메인에 종속되지 않으므로 common 패키지에 둠
public class MessageResponse {

    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}