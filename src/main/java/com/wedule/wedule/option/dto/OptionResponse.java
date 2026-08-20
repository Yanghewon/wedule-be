package com.wedule.wedule.option.dto;

import com.wedule.wedule.option.Option;
import com.wedule.wedule.option.OptionType;

// 옵션 조회 응답 DTO
public class OptionResponse {

    private Long id;
    private String name;
    private OptionType type;
    private int price;

    public OptionResponse(Option option) {
        this.id = option.getId();
        this.name = option.getName();
        this.type = option.getType();
        this.price = option.getPrice();
    }

    public Long getId() {
        return id;
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