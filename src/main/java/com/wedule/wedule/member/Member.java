package com.wedule.wedule.member;

import jakarta.persistence.*;
import lombok.Getter;

// 업체(작가) 계정 엔티티
// member 테이블과 매핑됨
@Getter
@Entity
@Table(name = "member")
public class Member {

    // 기본키(PK)
    // IDENTITY 전량: MYSQL의 auto-increment에 값 생성을 위임
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 업체명(작가명) - 필수
    @Column(nullable = false)
    private String businessName;

    // 로그인 계정으로 쓰이는 이메일
    // nullable = false: 필수값
    // unique = ture: 중복 가입 방지 (유일성 보장)
    @Column(nullable = false, unique = true)
    private String email;

    // 암호화된 비밀번호 저장
    @Column(nullable = false)
    private String password;

    // 연락처 - 선택 입력이라 별도 제약 없음
    private String phone;

    // 계약서에 들어갈 작가 사인 이미지 (프로필에 한 번 등록해두고 모든 계약서에 재사용)
    // 파일을 서버 디스크에 따로 저장하지 않고, 이미지 바이트 자체를 DB에 저장
    @Lob
    private byte[] signatureImage;

    // JPA가 프록시 객체를 생성할 때 리플렉션으로 호출하는 기본 생성자
    // public으로 열면 누구나 빈 값 Member를 만들 수 있어 위험하므로 protected로 제한
    protected Member() {
    }

    // 실제로 Member를 생성할 때 사용하는 생성자
    // 필수 정보를 모두 받아야만 객체가 만들어지도록 강제
    public Member(String email, String password, String businessName, String phone) {
        this.businessName = businessName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // 사인 이미지 등록/변경
    public void updateSignature(byte[] signatureImage) {
        this.signatureImage = signatureImage;
    }

    // Setter는 의도적으로 만들지 않음
    // 값 변경이 필요하면 changePassword() 같은 의미가 명확한 메서드를 추후 추가할 것
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getPhone() {
        return phone;
    }

    public byte[] getSignatureImage() { return signatureImage; }
}
