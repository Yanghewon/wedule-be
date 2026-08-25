package com.wedule.wedule.reservation.entity;

import com.wedule.wedule.member.Member;
import jakarta.persistence.*;

// 작가가 직접 등록하는 커스텀 예약 항목의 "정의"
// 예: "도착 예정 시간", "스냅 작가 유무", "요청사항"
// 실제 값은 각 예약마다 따로 저장됨 (ReservationCustomFieldValue)
@Entity
@Table(name = "custom_field")
public class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 항목을 등록한 작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 항목 라벨 (예: "도착 예정 시간")
    @Column(nullable = false)
    private String label;

    // 화면에 표시될 순서
    @Column(nullable = false)
    private int displayOrder;

    // JPA용 기본 생성자
    protected CustomField() {
    }

    // 실제 생성용 생성자
    // 파라미터 순서: member, label, displayOrder
    public CustomField(Member member, String label, int displayOrder) {
        this.member = member;
        this.label = label;
        this.displayOrder = displayOrder;
    }

    // 항목 정보를 수정하는 메서드 (member는 수정 대상이 아니므로 제외)
    public void update(String label, int displayOrder) {
        this.label = label;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getLabel() {
        return label;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}