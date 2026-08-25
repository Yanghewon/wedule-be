package com.wedule.wedule.reservation.repository;

import com.wedule.wedule.reservation.entity.CustomFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, Long> {
    // 특정 예약에 연결된 모든 커스텀 항목 값 조회
    List<CustomFieldValue> findByReservationId(Long reservationId);
}