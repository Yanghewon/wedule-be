package com.wedule.wedule.member;

import com.wedule.wedule.member.dto.MemberSignUpRequest;
import com.wedule.wedule.member.dto.MemberSignUpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 회원(업체) 관련 HTTP 요청을 받는 진입점
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // POST /api/members/signup
    // 요청 본문(JSON)을 MemberSignUpRequest로 받아서 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<MemberSignUpResponse> singUp(@RequestBody MemberSignUpRequest memberSignUpRequest) {
        Long memberId = memberService.signUp(
                memberSignUpRequest.getEmail(),
                memberSignUpRequest.getPassword(),
                memberSignUpRequest.getBusinessName(),
                memberSignUpRequest.getPhone()
        );
        return ResponseEntity.ok(new MemberSignUpResponse(memberId));
    }
}
