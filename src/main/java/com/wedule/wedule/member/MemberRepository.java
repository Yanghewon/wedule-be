package com.wedule.wedule.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Member 엔티티에 대한 DB 접근을 담당
// JpaRepository<Member, Long>을 상속받으면
// save(), findById(), findAll(), delete() 같은 기본 CRUD 메서드가 자동으로 생김
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 로그인 시 이메일로 회원을 조회하기 위한 메서드
    // 메서드 이름 규칙(findBy + 필드명)만 지키면
    // Spring Data JPA가 "email 컬럼으로 조회하는 쿼리"를 자동 생성
    Optional<Member> findByEmail(String email);
}
