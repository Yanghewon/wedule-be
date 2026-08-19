package com.wedule.wedule.member.dto;

// 로그인 요청 DTO
public class AuthLoginRequest {

    private String email;
    private String password;

    public AuthLoginRequest() {
    }

    public AuthLoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getpassword() {
        return password;
    }
}
