package com.wedule.wedule.reservation.dto;

import com.wedule.wedule.reservation.Reservation;
import com.wedule.wedule.reservation.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

// 예약 조회 응답 DTO
// Reservation 엔티티를 그대로 반환하지 않고 필요한 정보만 골라서 내려줌
public class ReservationResponse {

    private Long id;
    private String packageName;
    private String groomName;
    private String brideName;
    private String phone;
    private LocalDate weddingDate;
    private LocalTime weddingTime;
    private String venueName;
    private ReservationStatus status;

    // Reservation 엔티티를 받아서 응답 DTO로 변환하는 생성자
    // Controller/Service에서 매번 필드를 하나씩 옮겨 담지 않고, 이 생성자 하나로 변환 가능
    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.packageName = reservation.getPackageInfo().getName();
        this.groomName = reservation.getGroomName();
        this.brideName = reservation.getBrideName();
        this.phone = reservation.getPhone();
        this.weddingDate = reservation.getWeddingDate();
        this.weddingTime = reservation.getWeddingTime();
        this.venueName = reservation.getVenueName();
        this.status = reservation.getStatus();
    }

    public Long getId() { return id; }
    public String getPackageName() {
        return packageName;
    }
    public String getGroomName() { return groomName; }
    public String getBrideName() { return brideName; }
    public String getPhone() { return phone; }
    public LocalDate getWeddingDate() { return weddingDate; }
    public LocalTime getWeddingTime() { return weddingTime; }
    public String getVenueName() { return venueName; }
    public ReservationStatus getStatus() { return status; }
}