package com.wedule.wedule.reservation.service;

import com.wedule.wedule.reservation.dto.response.AiParseResponse;
import com.wedule.wedule.reservation.dto.response.OpenAiChatResponse;
import com.wedule.wedule.reservation.dto.response.ParsedCustomFieldResponse;
import com.wedule.wedule.reservation.dto.response.ReservationParseResponse;
import com.wedule.wedule.reservation.entity.CustomField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 붙여넣은 예약 양식 텍스트를 OpenAI API로 분석해서, 구조화된 값으로 변환하는 서비스
@Service
public class ReservationAiParsingService {

    // 이 클래스에서 발생하는 로그를 남기기 위한 Logger
    private static final Logger log = LoggerFactory.getLogger(ReservationAiParsingService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public ReservationAiParsingService(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model
    ) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.objectMapper = new ObjectMapper();
        this.model = model;
    }

    // customFields: 로그인한 작가가 등록해둔 커스텀 항목 목록
    public ReservationParseResponse parse(String rawText, List<CustomField> customFields) {
        try {
            String prompt = buildPrompt(customFields);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", prompt),
                            Map.of("role", "user", "content", rawText)
                    ),
                    "response_format", Map.of("type", "json_object")
            );

            OpenAiChatResponse response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(OpenAiChatResponse.class);

            String content = response.getChoices().get(0).getMessage().getContent();
            AiParseResponse aiResult = objectMapper.readValue(content, AiParseResponse.class);

            log.info("AI 파싱 성공");
            return toResponse(aiResult, customFields);

        } catch (Exception e) {
            // 외부 API 호출 실패는 우리 코드 버그가 아니라, 네트워크/API 키/크레딧 등
            // 외부 요인일 가능성이 높아서, 원인 추적을 위해 전체 예외 정보를 남겨둠
            // rawText는 고객 개인정보(이름, 연락처 등)를 포함할 수 있어, 앞 50자만 잘라서 기록
            log.error("AI 파싱 실패 - rawText 일부: {}",
                    rawText.substring(0, Math.min(rawText.length(), 50)), e);
            return new ReservationParseResponse();
        }
    }

    // 커스텀 항목 목록을 반영해서 매번 프롬프트를 새로 구성
    private String buildPrompt(List<CustomField> customFields) {
        String customFieldSection = customFields.isEmpty()
                ? "customFields 항목은 빈 객체 {}로 응답해."
                : "그리고 아래 커스텀 항목들도 텍스트에서 찾아서 customFields 객체 안에 라벨을 key로 하여 포함시켜:\n"
                + customFields.stream().map(CustomField::getLabel).collect(Collectors.joining(", "));

        return """
                너는 웨딩 스냅 예약 양식 텍스트에서 정보를 추출하는 도우미야.
                아래 항목을 찾아서 JSON으로만 응답해. 설명이나 다른 텍스트는 절대 포함하지 마.

                날짜 해석 규칙 (매우 중요):
                - 한국에서 날짜는 "연.월.일" 또는 "연/월/일" 순서로 표기하는 것이 관례야.
                - 예: "26.08.22"는 2026년 08월 22일을 의미해 (26년, 08월, 22일 순서).
                - 연도가 2자리(YY)면 반드시 20YY로 해석해 (26 -> 2026).
                - 절대 일(day)과 연도를 헷갈리지 마. 항상 첫 번째 숫자가 연도야.

                {
                  "groomName": "신랑 이름 (없으면 null)",
                  "brideName": "신부 이름 (없으면 null)",
                  "phone": "연락처 (없으면 null)",
                  "weddingDate": "예식 날짜, yyyy-MM-dd 형식 (없으면 null)",
                  "weddingTime": "예식 시간, HH:mm 형식 (없으면 null)",
                  "venueName": "예식 장소 (없으면 null)",
                  "customFields": { "항목라벨": "인식된 값 (없으면 null)" }
                }

                """ + customFieldSection;
    }

    // AI가 응답한 원시 결과를, 실제 응답 형태(ReservationParseResponse)로 변환
    private ReservationParseResponse toResponse(AiParseResponse aiResult, List<CustomField> customFields) {
        ReservationParseResponse response = new ReservationParseResponse();
        response.setGroomName(aiResult.getGroomName());
        response.setBrideName(aiResult.getBrideName());
        response.setPhone(aiResult.getPhone());
        response.setVenueName(aiResult.getVenueName());

        if (aiResult.getWeddingDate() != null) {
            response.setWeddingDate(java.time.LocalDate.parse(aiResult.getWeddingDate()));
        }
        if (aiResult.getWeddingTime() != null) {
            response.setWeddingTime(java.time.LocalTime.parse(aiResult.getWeddingTime()));
        }

        List<ParsedCustomFieldResponse> parsedCustomFields = new ArrayList<>();
        Map<String, String> aiCustomFields = aiResult.getCustomFields();
        if (aiCustomFields != null) {
            for (CustomField field : customFields) {
                String value = aiCustomFields.get(field.getLabel());
                if (value != null && !value.isBlank()) {
                    parsedCustomFields.add(new ParsedCustomFieldResponse(field.getId(), field.getLabel(), value));
                }
            }
        }
        response.setCustomFieldValues(parsedCustomFields);

        return response;
    }
}