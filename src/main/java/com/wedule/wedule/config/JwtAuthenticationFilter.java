package com.wedule.wedule.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// 요청 하나당 딱 한 번씩 실행되는 필터 (OncePerRequestFilter 상속)
// 모든 HTTP 요청이 컨트롤러에 도달하기 전에 이 필터를 거쳐감
// 여기서 하는 일은 딱 하나: "요청에 실려온 토큰을 보고, 이게 누구의 요청인지 서버에 등록해두는 것"
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1단계: 요청 헤더에서 토큰 문자열만 뽑아낸다.
        String token = resolveToken(request);

        // 2단계: 토큰이 존재하고, 그 토큰이 유효한지(서명 위조 안 됐는지, 만료 안 됐는지) 확인한다.
        if (token != null && jwtProvider.validateToken(token)) {

            // 3단계: 토큰이 진짜라고 확인됐으니, 그 안에 담겨있던 memberId를 꺼낸다.
            Long memberId = jwtProvider.getMemberId(token);

            // 4단계: "이 요청은 memberId번 회원이 보낸, 인증된 요청이다"라는 사실을
            // Spring Security 전체가 공유하는 저장소(SecurityContextHolder)에 등록한다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList());

            // 실제로 등록하는 부분.
            // 이렇게 등록해두면, 이후 이 요청이 처리되는 동안 어디서든(Controller 등)
            // "지금 요청 보낸 사람이 누구야?"를 SecurityContextHolder를 통해 물어볼 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5단계: 지금까지는 "인증 여부를 확인하고 표시만 해둔 것"이고,
        // 실제 요청 처리(Controller 실행 등)는 여기서 다음 필터에게 넘겨야 계속 진행된다.
        // 이 줄을 빼먹으면 요청이 여기서 멈춰버려서 응답이 영원히 안 온다.
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 "Bearer " 부분을 떼고 순수 토큰 값만 추출
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}