package com.wedule.wedule.reservation.dto;

// 예약이 진행되는 단계를 나타내는 상태값
// enum으로 만든 이유: 상태값을 String으로 관리하면 오타("계약완료" vs "계약완로")가 나도
// 컴파일러가 잡아주지 못하는데, enum은 정해진 값 외에는 아예 대입할 수 없어서 안전함
public enum ReservationStatus {

    INQUIRY,          // 문의: 고객이 처음 연락해온 단계
    CONSULTING,       // 상담중: 세부 사항 조율 중
    CONTRACT_PENDING, // 계약대기: 상담 끝나고 계약서 작성/서명 대기 중
    CONTRACTED,       // 계약완료: 계약금 입금 및 계약 완료
    SCHEDULED,        // 촬영예정: 계약 완료 후 촬영일을 기다리는 상태
    COMPLETED,        // 촬영완료: 실제 촬영이 끝난 상태
    EDITING,          // 보정중: 촬영본 보정 작업 진행 중
    DELIVERED,        // 전달완료: 보정 완료 후 결과물을 고객에게 전달함
    CANCELED           // 취소: 예약이 중간에 취소된 상태
}