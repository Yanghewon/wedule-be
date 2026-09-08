package com.wedule.wedule.member.dto;

import jakarta.validation.constraints.NotBlank;

// 로그인 요청 DTO
public class AuthLoginRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
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
