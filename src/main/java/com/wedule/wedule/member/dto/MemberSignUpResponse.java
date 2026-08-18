package com.wedule.wedule.member.dto;

// 회원가입 성공 시 클라이언트에게 돌려줄 응답 형태
// { "id": 1 } 같은 JSON으로 변환됨
public class MemberSignUpResponse {

    private Long id;

    public MemberSignUpResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}