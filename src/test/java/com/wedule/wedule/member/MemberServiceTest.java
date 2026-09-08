package com.wedule.wedule.member;

import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import com.wedule.wedule.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 정상적인_이메일이면_회원가입에_성공한다() {
        // given
        String email = "test@wedule.com";
        String rawPassword = "password1234";
        String businessName = "셀리에 스냅";
        String phone = "01012345678";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encoded-password");
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when & then: 예외 없이 정상적으로 실행되는지만 확인
        // (실제 id는 DB가 자동 채번하는 값이라, DB 없는 단위 테스트에서는 null일 수밖에 없음)
        org.assertj.core.api.Assertions.assertThatCode(() ->
                memberService.signUp(email, rawPassword, businessName, phone)
        ).doesNotThrowAnyException();

        // save가 실제로 호출됐는지, 어떤 값으로 호출됐는지 확인
        org.mockito.Mockito.verify(memberRepository).save(any(Member.class));
    }

    @Test
    void 이미_가입된_이메일이면_예외가_발생한다() {
        // given: 이미 가입되어 있는 회원이 존재하는 상황을 가짜로 만듦
        String email = "existing@wedule.com";
        Member existingMember = new Member(email, "encoded", "셀리에 스냅", "01012345678");

        // "이 이메일로 조회하면, 이미 존재하는 회원을 돌려달라"고 설정
        // -> signUp 내부의 중복 체크(if isPresent)가 true가 되도록 유도
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

        // when & then: 같은 이메일로 다시 가입을 시도   하면,
        // 정확히 IllegalArgumentException이, 정확히 이 메시지로 던져지는지 확인
        assertThatThrownBy(() ->
                memberService.signUp(email, "password1234", "셀리에 스냅", "01012345678")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }
}