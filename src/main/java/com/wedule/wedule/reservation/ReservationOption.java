package com.wedule.wedule.reservation;

import com.wedule.wedule.option.Option;
import jakarta.persistence.*;

// 예약과 옵션의 N:M 관계를 표현하는 연결 엔티티
// "어떤 예약이 어떤 옵션을 선택했는지" 한 줄(row)로 나타냄
@Entity
@Table(name = "reservation_option")
public class ReservationOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    protected ReservationOption() {
    }

    public ReservationOption(Reservation reservation, Option option) {
        this.reservation = reservation;
        this.option = option;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Option getOption() {
        return option;
    }
}