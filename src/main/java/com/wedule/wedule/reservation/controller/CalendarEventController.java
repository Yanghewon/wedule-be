package com.wedule.wedule.reservation.controller;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.reservation.dto.request.CalendarEventUpdateRequest;
import com.wedule.wedule.reservation.dto.response.CalendarEventResponse;
import com.wedule.wedule.reservation.service.CalendarEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-events")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    public CalendarEventController(CalendarEventService calendarEventService) {
        this.calendarEventService = calendarEventService;
    }

    // GET /api/calendar-events — 내 캘린더 일정 목록 조회
    @GetMapping
    public ResponseEntity<List<CalendarEventResponse>> getCalendarEvents(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(calendarEventService.getCalendarEvents(memberId));
    }

    // PUT /api/calendar-events/{id} — 일정 제목 수정
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateTitle(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody CalendarEventUpdateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        calendarEventService.updateTitle(memberId, id, request);
        return ResponseEntity.ok(new MessageResponse("일정 제목이 수정되었습니다🤍"));
    }
}