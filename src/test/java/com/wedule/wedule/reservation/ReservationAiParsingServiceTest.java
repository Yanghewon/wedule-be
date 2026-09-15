package com.wedule.wedule.reservation;

import com.wedule.wedule.reservation.dto.response.ReservationParseResponse;
import com.wedule.wedule.reservation.entity.CustomField;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.reservation.service.ReservationAiParsingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 실제 OpenAI API를 호출하는 통합 테스트
// OPENAI_API_KEY 환경변수가 설정되어 있을 때만 실행됨 (없는 환경에서는 자동으로 건너뜀)
// -> 비용이 발생하고 응답 시간이 걸리므로, Mock 기반 단위 테스트와는 별도로 취급
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class ReservationAiParsingServiceTest {

    private ReservationAiParsingService service;

    @BeforeEach
    void setUp() {
        // 실제 서비스가 동작할 때와 동일한 방식으로, 환경변수에서 API 키를 직접 읽어와 생성
        String apiKey = System.getenv("OPENAI_API_KEY");
        String model = "gpt-4.1-mini";
        service = new ReservationAiParsingService(apiKey, model);
    }

    @Test
    void 정형화된_텍스트에서_핵심_항목을_정확히_추출한다() {
        // given: 라벨이 명확한 표준 예약 양식 텍스트
        String rawText = """
                신랑/신부님 성함 : 박주영/김예지
                연락처 : 010-4073-1805
                예식 날짜 / 시간 : 2026.08.09 / 11:00
                예식 장소 : 상록아트홀
                """;

        // when: 실제로 OpenAI에 요청을 보내 파싱함 (커스텀 항목은 없는 상황)
        ReservationParseResponse response = service.parse(rawText, List.of());

        // then: AI가 각 항목을 정확히 인식했는지 확인
        assertThat(response.getGroomName()).isEqualTo("박주영");
        assertThat(response.getBrideName()).isEqualTo("김예지");
        assertThat(response.getPhone()).isEqualTo("010-4073-1805");
        assertThat(response.getVenueName()).isEqualTo("상록아트홀");
    }

    @Test
    void 두자리_연도_날짜표기를_2000년대로_정확히_해석한다() {
        // given: "26.08.22"처럼 두 자리 연도로 된 날짜 표기
        // (예전에 AI가 연/월/일 순서를 헷갈렸던 문제를 프롬프트 규칙으로 고친 부분을 검증)
        String rawText = "신랑/신부님 성함 : 이정우/한소율\n연락처 : 010-5523-8871\n예식 날짜 / 시간 : 26.08.22 / 13:00\n예식 장소 : 롯데호텔";

        // when
        ReservationParseResponse response = service.parse(rawText, List.of());

        // then: 2026년 8월 22일로 정확히 해석되어야 함 (연/월/일이 뒤바뀌지 않아야 함)
        assertThat(response.getWeddingDate()).isEqualTo(java.time.LocalDate.of(2026, 8, 22));
    }

    @Test
    void 라벨이_없어도_문맥으로_이름과_연락처를_인식한다() {
        // given: 라벨 없이 값만 나열된, 정규식으로는 처리 못 했던 형식
        String rawText = "박지훈, 오세연\n010-2841-6903\n서울 강남 더베일리에서 오후 2시에 만나요";

        // when
        ReservationParseResponse response = service.parse(rawText, List.of());

        // then: AI가 문맥을 이해해서, 최소한 이름과 연락처는 인식해내야 함
        assertThat(response.getGroomName()).isNotBlank();
        assertThat(response.getPhone()).isEqualTo("010-2841-6903");
    }

    @Test
    void 등록된_커스텀_항목도_함께_추출한다() throws Exception {
        // given: 작가가 등록해둔 커스텀 항목("도착 예정 시간")이 텍스트에 포함된 상황
        Member member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);
        CustomField arrivalTime = new CustomField(member, "도착 예정 시간", 1);
        setId(arrivalTime, 10L);

        String rawText = "신랑/신부님 성함 : 박주영/김예지\n연락처 : 010-4073-1805\n예식 날짜 / 시간 : 2026.08.09 / 11:00\n예식 장소 : 상록아트홀\n도착 예정 시간 : 12시";

        // when: 등록된 커스텀 항목 목록을 함께 전달
        ReservationParseResponse response = service.parse(rawText, List.of(arrivalTime));

        // then: customFieldValues 안에 "도착 예정 시간" 항목이 인식되어 담겨있어야 함
        assertThat(response.getCustomFieldValues())
                .anyMatch(cfv -> cfv.getLabel().equals("도착 예정 시간") && cfv.getValue().contains("12"));
    }

    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}