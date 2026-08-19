package com.wedule.wedule.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 생성
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long signUp(String email, String rawPassword, String bussinessName, String phone) {
        // 이메일 중복 체크
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);
        // Member 객체 생성 (암호화된 비밀번호로)
        Member member = new Member(email, encodedPassword, bussinessName, phone);
        // 저장
        Member savedMember = memberRepository.save(member);
        // 저장된 member id 반환
        return savedMember.getId();
    }
}
