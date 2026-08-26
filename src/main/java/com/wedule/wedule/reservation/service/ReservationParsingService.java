package com.wedule.wedule.reservation.service;

import com.wedule.wedule.reservation.dto.CoreFieldLabels;
import com.wedule.wedule.reservation.dto.response.ReservationParseResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 붙여넣은 예약 양식 텍스트를 분석해서, 구조화된 값으로 변환하는 서비스
// 실제로 DB에 저장하지는 않고, 미리보기용 결과만 반환함
@Service
public class ReservationParsingService {

    // "라벨 : 값" 형태의 줄을 찾는 정규식
    // 그룹1: 라벨 부분, 그룹2: 값 부분 (":" 기준으로 좌우 분리)
    private static final Pattern LINE_PATTERN = Pattern.compile("^(.+?)[:：]\\s*(.*)$");

    // 이름을 나눌 때 시도할 구분자 후보들 (순서대로 시도)
    private static final String[] NAME_SEPARATORS = {"/", ",", "、", "·"};

    // 날짜 형식을 인식할 때 시도할 패턴들 (순서대로 시도)
    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    private static final DateTimeFormatter[] TIME_FORMATS = {
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H시 mm분"),
            DateTimeFormatter.ofPattern("H시")
    };

    public ReservationParseResponse parse(String rawText) {
        ReservationParseResponse result = new ReservationParseResponse();

        // 1. 텍스트를 줄 단위로 쪼갬
        String[] lines = rawText.split("\\r?\\n");

        for (String line : lines) {
            if (line.isBlank()) {
                continue; // 빈 줄은 건너뜀
            }

            // 2. "라벨 : 값" 패턴에 맞는지 확인
            Matcher matcher = LINE_PATTERN.matcher(line.trim());
            if (!matcher.matches()) {
                continue; // 패턴에 안 맞는 줄은 그냥 무시
            }

            String label = matcher.group(1).trim();
            String value = matcher.group(2).trim();

            // 3. 이 라벨이 어떤 핵심 필드에 해당하는지 확인
            CoreFieldLabels coreField = CoreFieldLabels.fromLabel(label);
            if (coreField == null) {
                continue; // 우리가 아는 핵심 라벨이 아니면 건너뜀 (커스텀 항목 매칭은 다음 단계에서)
            }

            // 4. 필드 종류에 따라 다르게 처리
            switch (coreField) {
                case GROOM_BRIDE_NAME -> applyGroomBrideName(result, value);
                case PHONE -> result.setPhone(value);
                case WEDDING_DATE_TIME -> applyWeddingDateTime(result, value);
                case VENUE_NAME -> result.setVenueName(value);
            }
        }

        return result;
    }

    // "김수혜/박철희" 같은 값을 신랑/신부로 나눠서 채움
    // 첫 번째 = 신랑, 두 번째 = 신부로 고정
    private void applyGroomBrideName(ReservationParseResponse result, String value) {
        for (String separator : NAME_SEPARATORS) {
            if (value.contains(separator)) {
                String[] names = value.split(Pattern.quote(separator), 2);
                if (names.length == 2) {
                    result.setGroomName(names[0].trim());
                    result.setBrideName(names[1].trim());
                    return;
                }
            }
        }
        // 어떤 구분자로도 못 나누면, 통째로 신랑 이름 자리에만 넣어두고 나머지는 빈 값으로 남김
        // (사용자가 미리보기에서 직접 수정하도록)
        result.setGroomName(value);
    }

    // "2026.09.20 / 13:40" 같은 값을 날짜와 시간으로 나눠서 파싱
    private void applyWeddingDateTime(ReservationParseResponse result, String value) {
        String[] parts = value.split("/");
        if (parts.length != 2) {
            return; // 형식이 예상과 다르면 그냥 파싱 실패로 남김 (빈 값)
        }

        String datePart = parts[0].trim();
        String timePart = parts[1].trim();

        LocalDate date = tryParseDate(datePart);
        LocalTime time = tryParseTime(timePart);

        if (date != null) {
            result.setWeddingDate(date);
        }
        if (time != null) {
            result.setWeddingTime(time);
        }
    }

    // 여러 날짜 형식을 순서대로 시도해보고, 처음 성공하는 것을 반환
    // 전부 실패하면 null 반환
    private LocalDate tryParseDate(String text) {
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(text, formatter);
            } catch (Exception e) {
                // 이 형식으로는 안 맞으니 다음 형식을 시도
            }
        }
        return null;
    }

    private LocalTime tryParseTime(String text) {
        for (DateTimeFormatter formatter : TIME_FORMATS) {
            try {
                return LocalTime.parse(text, formatter);
            } catch (Exception e) {
                // 다음 형식 시도
            }
        }
        return null;
    }
}