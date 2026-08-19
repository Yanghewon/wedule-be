package com.wedule.wedule.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// JWT 토큰을 생성하고 검증하는 역할만 담당하는 클래스
// "누가 로그인했는지 확인하는 로직"이 아니라 "토큰 자체를 다루는 도구"라는 점에서
// MemberService와 역할을 분리함
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMillis = 1000 * 60 * 60 * 24;

    // application.properties에 등록한 시크릿 값을 주입받아 서명 키를 생성
    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 토큰을 생성
    public String createToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // 토큰이 유효한지 검증(서명이 맞는지, 만료되지 않았는지)
    public boolean validateToken(String token) {
        // 토큰을 열어서 해석
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }  catch (JwtException | IllegalArgumentException e) {
            // 안 맞거나 만료 시간이 지났으면 자동으로 예외를 던짐
            return false;
        }
    }

    // 토큰에서 memberId를 꺼내옴
    public Long getMemberId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }
}
