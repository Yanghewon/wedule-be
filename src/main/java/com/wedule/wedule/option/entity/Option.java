package com.wedule.wedule.option.entity;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.option.OptionType;
import jakarta.persistence.*;

// 추가 옵션 / 할인 옵션 엔티티
// 작가(Member)별로 자신만의 옵션 목록을 등록해서 관리
@Entity
@Table(name = "reservation_option_item")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 옵션을 등록한 작가(업체)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 옵션 이름 (예: "2부 촬영 추가", "블로그 후기 할인")
    @Column(nullable = false)
    private String name;

    // 옵션 종류 (추가옵션 / 할인)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionType type;

    // 옵션 금액
    // 추가옵션은 양수(+150000), 할인은 음수(-50000)로 저장해서
    // 나중에 최종 금액 계산 시 단순히 다 더하기만 하면 되도록 설계
    @Column(nullable = false)
    private int price;

    // JPA용 기본 생성자
    protected Option() {
    }

    // 실제 생성용 생성자
    // 파라미터 순서: member, name, type, price
    public Option(Member member, String name, OptionType type, int price) {
        this.member = member;
        this.name = name;
        this.type = type;
        this.price = price;
    }

    // 옵션 정보 수정 메서드 (member는 수정 대상이 아니므로 제외)
    public void update(String name, OptionType type, int price) {
        this.name = name;
        this.type = type;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getName() {
        return name;
    }

    public OptionType getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }
}