package com.wedule.wedule.reservation.entity;

import jakarta.persistence.*;

// 특정 예약에 대한, 특정 커스텀 항목의 실제 입력값
// 예: 5번 예약의 "도착 예정 시간" 항목 값은 "12시"
@Entity
@Table(name = "custom_field_value")
public class CustomFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    private CustomField customField;

    // 실제 입력된 값(자유 텍스트)
    @Column(nullable = false)
    private String value;

    protected   CustomFieldValue() {
    }

    public CustomFieldValue(Reservation reservation, CustomField customField, String value) {
        this.reservation = reservation;
        this.customField = customField;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public CustomField getCustomField() {
        return customField;
    }

    public String getValue() {
        return value;
    }
}
