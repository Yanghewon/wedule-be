package com.wedule.wedule.reservation.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

// 예약이 계약 완료(CONTRACTED)될 때 자동 생성되는 캘린더 일정
@Entity
@Table(name = "calendar_event")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 이 일정이 어느 예약에서 비롯됐는지
    // 캘린더에서 이 일정을 클릭했을 때, 이 id로 예약 상세 조회 API를 다시 호출하면 됨
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    // 일정 제목 (예 "김예지 / 더채플앳청담 / 14:30")
    @Column(nullable = false)
    private String title;

    // 일정 날짜
    @Column(nullable = false)
    private LocalDate EventDate;

    protected CalendarEvent() {
    }

    public CalendarEvent(Reservation reservation, String title, LocalDate EventDate) {
        this.reservation = reservation;
        this.title = title;
        this.EventDate = EventDate;
    }
    // 일정 제목을 수동으로 수정하는 메서드
    // 자동 생성은 신부이름 기준이지만, 작가가 원하는 대로 직접 고칠 수 있게 함
    public void updateTitle(String title) {
        this.title = title;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Reservation getReservation() {
        return reservation;
    }
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDate getEventDate() {
        return EventDate;
    }
    public void setEventDate(LocalDate eventDate) {
        EventDate = eventDate;
    }
}
