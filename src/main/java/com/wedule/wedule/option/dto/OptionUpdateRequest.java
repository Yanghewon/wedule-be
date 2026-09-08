package com.wedule.wedule.option.dto;

import com.wedule.wedule.option.OptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 옵션 수정 요청 DTO
public class OptionUpdateRequest {

    @NotBlank(message = "옵션 이름은 필수입니다.")
    private String name;

    @NotNull(message = "옵션 종류를 선택해주세요.")
    private OptionType type;

    private int price;

    public OptionUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OptionType getType() {
        return type;
    }

    public void setType(OptionType type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}