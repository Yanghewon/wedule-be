package com.wedule.wedule.reservation.dto.request;

// 캘린더 일정 제목 수정 요청 DTO
public class CalendarEventUpdateRequest {

    private String title;

    public CalendarEventUpdateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}