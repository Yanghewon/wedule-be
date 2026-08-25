package com.wedule.wedule.reservation.repository;

import com.wedule.wedule.reservation.entity.CustomField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
    // 특정 작가가 등록한 커스텀 항목 목록 조회 (표시 순서대로 정렬)
    List<CustomField> findByMemberIdOrderByDisplayOrderAsc(Long memberId);
}
