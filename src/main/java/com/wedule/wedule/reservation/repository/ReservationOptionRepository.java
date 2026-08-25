package com.wedule.wedule.reservation.repository;

import com.wedule.wedule.reservation.entity.ReservationOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationOptionRepository extends JpaRepository<ReservationOption, Long> {

    // 특정 예약에 연결된 모든 옵션 연결 정보 조회
    List<ReservationOption> findByReservationId(Long reservationId);
}