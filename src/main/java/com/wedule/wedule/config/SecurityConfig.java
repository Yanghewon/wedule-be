package com.wedule.wedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security의 전역 보안 설정을 담당하는 클래스
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호를 암호화/검증할 때 사용할 인코더 Bean으로 등록
    // BCrypt: 같은 비밀번호를 넣어도 매번 다른 암호화 결과가 나오는 단방향 해시 알고리즘
    // (원문 복호화가 불가능하고, 검증은 matches()로 비교하는 방식)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 지금 단계에서는 회원가입/로그인 API는 인증 없이 접근 가능하도록 임시로 허용
    // JWT 인증을 실제로 붙이는 시점에 이 부분을 다시 손볼 예정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()
                );
        return http.build();
    }


}
