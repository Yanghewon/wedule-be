package com.wedule.wedule.packages.dto;

import com.wedule.wedule.packages.Package;

// 패키지 조회 응답 DTO
public class PackageResponse {
    private Long id;
    private String name;
    private int price;
    private String courseGuide;
    private String shootingTime;
    private String composition;

    // Package 엔티티를 응답 형태로 변환하는 생성자
    // (package는 자바 예약어라 변수명은 pkg로 사용)
    public PackageResponse(Package pkg) {
        this.id = pkg.getId();
        this.name = pkg.getName();
        this.price = pkg.getPrice();
        this.courseGuide = pkg.getCourseGuide();
        this.shootingTime = pkg.getShootingTime();
        this.composition = pkg.getComposition();
    }

    public Long getId() {
        return id;
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