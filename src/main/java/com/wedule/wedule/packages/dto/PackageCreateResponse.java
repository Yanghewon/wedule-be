package com.wedule.wedule.packages.dto;

// 패키지 생성 성공 응답
// 생성된 패키지의 id와, 사용자에게 보여줄 성공 메시지를 함께 담음
public class PackageCreateResponse {

    private Long id;
    private String message;

    public PackageCreateResponse(Long id, String message) {
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