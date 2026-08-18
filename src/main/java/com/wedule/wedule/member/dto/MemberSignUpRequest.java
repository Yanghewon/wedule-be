package com.wedule.wedule.member.dto;

// 회원가입 요청으로 클라이언트가 보내는 JSON을 받은 DTO
// 프론트엔드가 { "email": "...", "password": "...", "businessName": "...", "phone": "..." }
// 형태로 요청을 보내면, Spring이 이 필드들에 자동으로 값을 채워줌
public class MemberSignUpRequest {

    private String email;
    private String password;
    private String businessName;
    private String phone;

    // JSON -> 객체로 변환할 때 Jackson 라이브러리가 필요로 하는 기본 생성자
    public MemberSignUpRequest() {
    }

    public MemberSignUpRequest(String email, String password, String businessName, String phone, String passwordEncoder) {
        this.email = email;
        this.password = password;
        this.businessName = businessName;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPhone() {
        return phone;
    }
}
