package com.wedule.wedule.reservation.dto;

// 핵심 항목의 고정 라벨 문구와, 그게 어떤 필드에 대응하는지를 함께 관리
public enum CoreFieldLabels {

    GROOM_BRIDE_NAME("신랑/신부님 성함"),
    PHONE("연락처"),
    WEDDING_DATE_TIME("예식 날짜 / 시간"),
    VENUE_NAME("예식 장소");

    private final String label;

    CoreFieldLabels(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CoreFieldLabels fromLabel(String label) {
        for (CoreFieldLabels field : values()) {
            if (field.label.equals(label.trim())) {
                return field;
            }
        }
        return null;
    }
}