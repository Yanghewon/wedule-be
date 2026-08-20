package com.wedule.wedule.option.dto;

import com.wedule.wedule.option.OptionType;

// 옵션 수정 요청 DTO
public class OptionUpdateRequest {

    private String name;
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