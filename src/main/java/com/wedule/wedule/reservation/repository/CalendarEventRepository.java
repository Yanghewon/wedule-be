package com.wedule.wedule.reservation.repository;

import com.wedule.wedule.reservation.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    // 특정 회원의 캘린더 일정 전체 조회 (예약을 통해 회원과 연결됨)
    List<CalendarEvent> findByReservationMemberId(Long memberId);

    // 특정 예약에 이미 캘린더 일정이 있는지 확인할 때 사용
    Optional<CalendarEvent> findByReservationId(Long reservationId);
}
