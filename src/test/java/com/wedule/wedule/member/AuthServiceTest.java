package com.wedule.wedule.member;

import com.wedule.wedule.config.JwtProvider;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import com.wedule.wedule.member.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// 실제 DB, JWT 라이브러리 없이 AuthService의 로그인 로직만 단독으로 검증하는 단위 테스트
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void 이메일과_비밀번호가_일치하면_토큰을_발급한다() {
        // given
        String email = "test@wedule.com";
        String rawPassword = "password1234";
        String encodedPassword = "encoded-password";

        Member member = new Member(email, encodedPassword, "셀리에 스냅", "01012345678");

        // "이 이메일로 조회하면, 위에서 만든 회원을 돌려달라"
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        // "입력한 비밀번호와 저장된 암호화 비밀번호를 비교하면, 일치한다고 답해달라"
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        // "토큰 발급을 요청하면, 이 가짜 토큰 문자열을 돌려달라"
        when(jwtProvider.createToken(member.getId())).thenReturn("fake-jwt-token");

        // when
        String token = authService.login(email, rawPassword);

        // then: 발급된 토큰이 우리가 설정해둔 가짜 토큰과 정확히 일치하는지 확인
        assertThat(token).isEqualTo("fake-jwt-token");
    }

    @Test
    void 존재하지_않는_이메일이면_예외가_발생한다() {
        // given: 이 이메일로 조회하면 아무것도 없다고 답하도록 설정
        String email = "unknown@wedule.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(email, "anyPassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    void 비밀번호가_틀리면_예외가_발생한다() {
        // given: 이메일은 존재하지만, 비밀번호 비교 결과는 불일치로 설정
        String email = "test@wedule.com";
        String wrongPassword = "wrongPassword";
        Member member = new Member(email, "encoded-password", "셀리에 스냅", "01012345678");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(wrongPassword, "encoded-password")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(email, wrongPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
}