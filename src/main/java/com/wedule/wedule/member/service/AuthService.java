package com.wedule.wedule.member.service;

import com.wedule.wedule.config.JwtProvider;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public String login(String email, String password) {
        //1. 이메일로 회원 조회, 없으면 예외
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 이메일: {}", email);
                    return new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
                });

        //2. 입력한 비밀번호와 저장된 암호화 비밀번호를 비교
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: {}", email);
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        //3. 검증 통과 시 토큰 발급
        log.info("로그인 성공: {}", email);
        return jwtProvider.createToken(member.getId());
    }
}