package com.wedule.wedule.option;

// 옵션의 종류
// ADDON: 추가 촬영, 2부 촬영 등 금액이 더해지는 옵션
// DISCOUNT: 블로그 후기, SNS 동의 등 금액이 할인되는 옵션
// 둘 다 "예약에 여러 개 선택해서 붙는 항목"이라는 구조가 같아서
// 하나의 Option 테이블로 통합하고, 이 타입 값으로만 구분하기로 설계함
public enum OptionType {
    ADDON, DISCOUNT
}