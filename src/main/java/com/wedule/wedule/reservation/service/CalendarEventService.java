package com.wedule.wedule.reservation.service;

import com.wedule.wedule.reservation.dto.response.CalendarEventResponse;
import com.wedule.wedule.reservation.dto.request.CalendarEventUpdateRequest;
import com.wedule.wedule.reservation.entity.CalendarEvent;
import com.wedule.wedule.reservation.repository.CalendarEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;

    public CalendarEventService(CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }

    // 로그인한 회원의 캘린더 일정 전체 조회
    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getCalendarEvents(Long memberId) {
        return calendarEventRepository.findByReservationMemberId(memberId).stream()
                .map(CalendarEventResponse::new)
                .collect(Collectors.toList());
    }

    // 일정 제목 수정 (본인 소유인지 검증 포함)
    @Transactional
    public void updateTitle(Long memberId, Long calendarEventId, CalendarEventUpdateRequest request) {
        CalendarEvent calendarEvent = calendarEventRepository.findById(calendarEventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        if (!calendarEvent.getReservation().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 일정입니다.");
        }

        calendarEvent.updateTitle(request.getTitle());
    }
}
