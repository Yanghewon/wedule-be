package com.wedule.wedule.reservation.repository;

import com.wedule.wedule.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 회원(업체)의 예약 목록만 조회
    List<Reservation> findByMemberId(Long memberId);
    boolean existsByMemberIdAndWeddingDateAndWeddingTime(Long memberId, LocalDate weddingDate, LocalTime weddingTime);
}