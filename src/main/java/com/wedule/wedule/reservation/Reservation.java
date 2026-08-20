package com.wedule.wedule.reservation;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.packages.Package;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

// 예약 정보를 담는 엔티티
// reservation 테이블과 매핑됨
@Entity
@Table(
        name = "reservation",
        // member_id + wedding_date + wedding_time 조합이 항상 유일하도록 DB에 강제
        // 이렇게 해두면, 동시에 같은 시간대 예약 요청이 두 개 들어와도
        // 둘 중 하나는 반드시 DB 레벨에서 거부됨 (타이밍 문제와 무관하게 안전)
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_wedding_datetime",
                columnNames = {"member_id", "wedding_date", "wedding_time"}
        )
)
public class Reservation {

    // 기본키(PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 예약이 어느 업체(회원) 소속인지 나타내는 관계
    // @ManyToOne: 여러 Reservation이 하나의 Member에 속함 (N:1)
    // fetch = LAZY: Reservation을 조회할 때 Member 정보를 즉시 같이 가져오지 않고,
    //               실제로 member.getXxx()를 호출하는 시점에 필요할 때만 조회함
    //               (기본값 EAGER로 두면 Reservation 하나 조회할 때마다 불필요하게 Member까지 매번 조회하게 됨)
    // @JoinColumn: 실제 DB 테이블에서는 member_id라는 외래키(FK) 컬럼으로 저장됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 이 예약에서 선택한 촬영 패키지
    // Reservation N : 1 Package 관계 (여러 예약이 같은 패키지를 선택할 수 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package packageInfo;
    // 신랑 이름
    @Column(nullable = false)
    private String groomName;

    // 신부 이름
    @Column(nullable = false)
    private String brideName;

    // 예약자(고객) 연락처
    @Column(nullable = false)
    private String phone;

    // 예식 날짜
    // LocalDate를 쓰는 이유: String으로 저장하면 "8/9", "2026-08-09" 등 형식이 제멋대로일 수 있는데,
    // LocalDate는 날짜 형식을 강제하고, 날짜 비교/정렬 같은 연산도 훨씬 안전하고 쉬워짐
    @Column(nullable = false)
    private LocalDate weddingDate;

    // 예식 시간
    @Column(nullable = false)
    private LocalTime weddingTime;

    // 예식 장소
    @Column(nullable = false)
    private String venueName;

    // 예약 진행 상태
    // @Enumerated(EnumType.STRING): enum 값을 DB에 문자열("CONTRACTED" 등)로 저장
    // 이 어노테이션이 없으면 기본적으로 enum 선언 순서의 숫자(0, 1, 2...)로 저장되는데,
    // 그러면 나중에 enum에 값을 추가/순서변경했을 때 기존 저장된 숫자의 의미가 틀어져버림
    // 문자열로 저장하면 사람이 봐도 무슨 상태인지 바로 알 수 있고, 이런 위험도 없음
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // JPA가 프록시 객체를 생성할 때 필요한 기본 생성자
    // 무분별한 빈 객체 생성을 막기 위해 protected로 제한 (Member 엔티티와 동일한 이유)
    protected Reservation() {
    }

    // 예약을 실제로 생성할 때 사용하는 생성자
    // 새로 생성되는 예약은 항상 "문의(INQUIRY)" 상태로 시작한다고 가정하고 고정해둠
    public Reservation(Member member, Package packageInfo, String groomName, String brideName, String phone,
                       LocalDate weddingDate, LocalTime weddingTime, String venueName) {
        this.member = member;
        this.packageInfo = packageInfo;
        this.groomName = groomName;
        this.brideName = brideName;
        this.phone = phone;
        this.weddingDate = weddingDate;
        this.weddingTime = weddingTime;
        this.venueName = venueName;
        this.status = ReservationStatus.INQUIRY;
    }

    // 예약 상태를 변경하는 메서드
    public void changeStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Package getPackageInfo() { return packageInfo; }

    public String getGroomName() {
        return groomName;
    }

    public String getBrideName() {
        return brideName;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public LocalTime getWeddingTime() {
        return weddingTime;
    }

    public String getVenueName() {
        return venueName;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}