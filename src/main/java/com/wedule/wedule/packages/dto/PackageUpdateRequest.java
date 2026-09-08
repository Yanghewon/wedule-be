package com.wedule.wedule.packages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// 패키지 수정 요청 DTO
public class PackageUpdateRequest {

    @NotBlank(message = "패키지 이름은 필수입니다.")
    private String name;

    @Positive(message = "가격은 0보다 커야 합니다.")
    private int price;

    private String shootingTime;

    @NotBlank(message = "구성 내용은 필수입니다.")
    private String composition;

    @NotBlank(message = "촬영 코스 안내는 필수입니다.")
    private String courseGuide;

    public PackageUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCourseGuide() {
        return courseGuide;
    }

    public void setCourseGuide(String courseGuide) {
        this.courseGuide = courseGuide;
    }

    public String getShootingTime() {
        return shootingTime;
    }

    public void setShootingTime(String shootingTime) {
        this.shootingTime = shootingTime;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }
}