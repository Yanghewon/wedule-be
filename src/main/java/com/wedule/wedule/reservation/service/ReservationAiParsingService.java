package com.wedule.wedule.reservation.service;

import com.wedule.wedule.reservation.dto.response.OpenAiChatResponse;
import com.wedule.wedule.reservation.dto.response.ReservationParseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class ReservationAiParsingService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    // AI에게 역할과 출력 형식을 지시하는 고정 프롬프트
    // "이런 필드들을 이런 형식의 JSON으로만 응답하라"고 명확히 지정해야
    // AI가 엉뚱한 형식으로 응답하는 걸 최대한 방지할 수 있음
    private static final String SYSTEM_PROMPT = """
            너는 웨딩 스냅 예약 양식 텍스트에서 정보를 추출하는 도우미야.
            아래 항목을 찾아서 JSON으로만 응답해. 설명이나 다른 텍스트는 절대 포함하지 마.

            {
              "groomName": "신랑 이름 (없으면 null)",
              "brideName": "신부 이름 (없으면 null)",
              "phone": "연락처 (없으면 null)",
              "weddingDate": "예식 날짜, yyyy-MM-dd 형식 (없으면 null)",
              "weddingTime": "예식 시간, HH:mm 형식 (없으면 null)",
              "venueName": "예식 장소 (없으면 null)"
            }
            """;

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

    public ReservationParseResponse parse(String rawText) {
        try {
            // 1. OpenAI에게 보낼 요청 body 구성
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", rawText)
                    ),
                    "response_format", Map.of("type", "json_object")
            );

            // 2. 실제 API 호출
            OpenAiChatResponse response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(OpenAiChatResponse.class);

            // 3. 응답 안의 JSON 문자열을 우리가 원하는 DTO로 변환
            String content = response.getChoices().get(0).getMessage().getContent();
            return objectMapper.readValue(content, ReservationParseResponse.class);

        } catch (Exception e) {
            // AI API 호출이 실패하거나(네트워크 문제, 키 오류 등),
            // 응답 형식이 예상과 달라 파싱이 실패하면 빈 결과를 반환
            // (예약 자체가 실패하는 게 아니라, 자동 채움만 실패하고 사용자가 수동으로 입력하면 됨)
            e.printStackTrace();
            return new ReservationParseResponse();
        }
    }
}
