package com.wedule.wedule.contract.entity;

import com.wedule.wedule.reservation.entity.Reservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// 한 예약에 대해 실제로 생성된 계약서
// content는 ContractTemplate에서 가져온 내용을 기본값으로 하되, 이 계약서에서만 수정 가능
@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStyle style;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Contract() {
    }

    public Contract(Reservation reservation, ContractStyle style, String content) {
        this.reservation = reservation;
        this.style = style;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // 같은 예약의 계약서를 다시 생성/수정할 때 사용
    public void update(ContractStyle style, String content) {
        this.style = style;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ContractStyle getStyle() {
        return style;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}