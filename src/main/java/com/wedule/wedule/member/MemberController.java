package com.wedule.wedule.member;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.member.dto.MemberSignUpRequest;
import com.wedule.wedule.member.dto.MemberSignUpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    // POST /api/members/signature — 작가 사인 이미지 업로드
// @RequestParam("file") MultipartFile: JSON이 아니라 실제 파일(이미지)을 받을 때 쓰는 방식
    @PostMapping("/signature")
    public ResponseEntity<MessageResponse> uploadSignature(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Long memberId = (Long) authentication.getPrincipal();
        memberService.uploadSignature(memberId, file.getBytes());
        return ResponseEntity.ok(new MessageResponse("사인이 등록되었습니다."));
    }
}
