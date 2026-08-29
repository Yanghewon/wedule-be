package com.wedule.wedule.reservation.dto.response;

// 파싱 결과 중 커스텀 항목 하나에 대한 값
// customFieldId를 같이 내려줘야, 프론트에서 예약 생성 요청을 만들 때
// 어떤 항목의 값인지 바로 매칭할 수 있음
public class ParsedCustomFieldResponse {

    private Long customFieldId;
    private String label;
    private String value;

    public ParsedCustomFieldResponse(Long customFieldId, String label, String value) {
        this.customFieldId = customFieldId;
        this.label = label;
        this.value = value;
    }

    public Long getCustomFieldId() {
        return customFieldId;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}