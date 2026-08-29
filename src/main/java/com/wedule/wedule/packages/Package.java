package com.wedule.wedule.packages;

import com.wedule.wedule.member.Member;
import jakarta.persistence.*;

// 촬영 패키지 엔티티 (STANDARD, PREMIUM 등)
// 작가(Member)별로 자신만의 패키지 목록을 가질 수 있음
@Entity
@Table(name = "package")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 패키지가 어느 작가(업체) 소속인지
    // Reservation이 Member를 참조했던 것과 동일한 패턴
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 패키지 이름 (예: "STANDARD", "PREMIUM")
    @Column(nullable = false)
    private String name;

    // 패키지 가격
    @Column(nullable = false)
    private int price;

    // 촬영 코스 안내 - 시간대별 진행 순서를 안내하는 자유 텍스트
    // 예: "1시간 30분 전 도착 - 예식 - 플라워샤워 - 원판까지 진행 (총 2시간)"
    // 내용이 길어질 수 있어 @Lob으로 TEXT 타입 지정 (기본 VARCHAR는 길이 제한이 있음)
    @Lob
    @Column(nullable = false)
    private String courseGuide;

    // 촬영 소요 시간 (예: "2시간")
    // 지금은 자유 형식 문자열로 두고, 나중에 실제로 시간 계산이 필요해지면
    // Duration이나 int(분 단위) 타입으로 바꾸는 걸 고려할 수 있음
    @Column(nullable = false)
    private String shootingTime;

    // 패키지 구성 (제공 상품) - 자유 텍스트
    // 예: "원본 전체 전달, 액자 1개, 앨범 1개, USB 1개"
    // 항목이 정해진 개수가 아니고 단순 표시용 정보라 자유 텍스트로 저장
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String composition;

    // JPA가 프록시 객체를 생성할 때 필요로 하는 기본 생성자
    // 무분별한 빈 객체 생성을 막기 위해 protected로 제한
    // (반드시 아래 값 채우는 생성자와 별도로 존재해야 함 - 이게 없으면
    //  "no-arg constructor" 에러가 발생함)
    protected Package() {
    }

    // 실제로 패키지를 생성할 때 사용하는 생성자
    // 파라미터 순서: member, name, price, courseGuide, shootingTime, composition
    // (Service에서 이 생성자를 호출할 때도 반드시 이 순서를 그대로 지켜야 함)
    public Package(Member member, String name, int price, String courseGuide, String shootingTime, String composition) {
        this.member = member;
        this.name = name;
        this.price = price;
        this.courseGuide = courseGuide;
        this.shootingTime = shootingTime;
        this.composition = composition;
    }

    // 패키지 정보를 통째로 수정하는 메서드
    // setter 여러 개를 따로 열어두지 않고, "패키지 정보를 수정한다"는
    // 하나의 의도가 분명한 메서드로 묶어서 제공
    // 파라미터 순서는 위 생성자와 동일하게 맞춤 (member는 수정 대상이 아니라 제외)
    public void update(String name, int price, String courseGuide, String shootingTime, String composition) {
        this.name = name;
        this.price = price;
        this.courseGuide = courseGuide;
        this.shootingTime = shootingTime;
        this.composition = composition;
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

    public int getPrice() {
        return price;
    }

    public String getCourseGuide() {
        return courseGuide;
    }

    public String getShootingTime() {
        return shootingTime;
    }

    public String getComposition() {
        return composition;
    }
}