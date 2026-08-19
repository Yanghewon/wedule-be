package com.wedule.wedule.member.dto;

// 로그인 성공 시 응답 DTO
// { "token": "eudljw..."}
public class AuthLoginResponse {
    private String token;

    public AuthLoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
