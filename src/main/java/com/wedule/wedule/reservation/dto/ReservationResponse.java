package com.wedule.wedule.reservation.dto;

import com.wedule.wedule.reservation.Reservation;
import com.wedule.wedule.reservation.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 예약 조회 응답 DTO
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
    private List<String> optionNames;

    // Reservation 엔티티와, 별도로 조회한 옵션 이름 목록을 함께 받아서 응답 DTO로 변환
    // (Reservation 엔티티 자체에는 옵션 이름 목록이 없어서, Service에서 따로 조회해 넘겨받아야 함)
    public ReservationResponse(Reservation reservation, List<String> optionNames) {
        this.id = reservation.getId();
        this.packageName = reservation.getPackageInfo().getName();
        this.groomName = reservation.getGroomName();
        this.brideName = reservation.getBrideName();
        this.phone = reservation.getPhone();
        this.weddingDate = reservation.getWeddingDate();
        this.weddingTime = reservation.getWeddingTime();
        this.venueName = reservation.getVenueName();
        this.status = reservation.getStatus();
        this.optionNames = optionNames;
    }

    public Long getId() { return id; }
    public String getPackageName() { return packageName; }
    public String getGroomName() { return groomName; }
    public String getBrideName() { return brideName; }
    public String getPhone() { return phone; }
    public LocalDate getWeddingDate() { return weddingDate; }
    public LocalTime getWeddingTime() { return weddingTime; }
    public String getVenueName() { return venueName; }
    public ReservationStatus getStatus() { return status; }
    public List<String> getOptionNames() { return optionNames; }
}