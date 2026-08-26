package com.wedule.wedule.reservation.dto.response;

import com.wedule.wedule.reservation.entity.CalendarEvent;

import java.time.LocalDate;

public class CalendarEventResponse {

    private Long id;
    private Long reservationId;
    private String title;
    private LocalDate eventDate;

    public CalendarEventResponse(CalendarEvent calendarEvent) {
        this.id = calendarEvent.getId();
        this.reservationId = calendarEvent.getReservation().getId();
        this.title = calendarEvent.getTitle();
        this.eventDate = calendarEvent.getEventDate();
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }
}