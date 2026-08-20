package com.wedule.wedule.option;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    // 특정 작가가 등록한 옵션 목록만 조회
    List<Option> findByMemberId(Long memberId);
}