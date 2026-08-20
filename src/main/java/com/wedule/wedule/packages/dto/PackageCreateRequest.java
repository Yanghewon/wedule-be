package com.wedule.wedule.packages.dto;

// 패키지 생성 요청 DTO
public class PackageCreateRequest {

    private String name;
    private int price;
    private String courseGuide;
    private String shootingTime;
    private String composition;

    // Jackson이 JSON -> 객체 변환 시 필요
    public PackageCreateRequest() {
    }

    public String getName() {
        return name;
    }

    // Jackson이 JSON 값을 필드에 채워 넣을 때 이 메서드들을 사용함
    // getter만 있으면 값을 못 채울 수 있어 반드시 같이 있어야 함
    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getShootingTime() {
        return shootingTime;
    }

    public void setShootingTime(String shootingTime) {
        this.shootingTime = shootingTime;
    }

    public String getCourseGuide() {
        return courseGuide;
    }

    public void setCourseGuide(String courseGuide) {
        this.courseGuide = courseGuide;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }
}