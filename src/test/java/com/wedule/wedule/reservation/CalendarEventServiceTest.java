package com.wedule.wedule.reservation;

import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.packages.entity.Package;
import com.wedule.wedule.reservation.dto.request.CalendarEventUpdateRequest;
import com.wedule.wedule.reservation.entity.CalendarEvent;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.repository.CalendarEventRepository;
import com.wedule.wedule.reservation.service.CalendarEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

// CalendarEventService의 조회/수정 로직을 DB 없이 단위 테스트로 검증
@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    // 진짜 DB 대신 사용할 가짜(Mock) Repository
    @Mock private CalendarEventRepository calendarEventRepository;

    // 위 가짜 객체를 자동으로 주입받아 만들어지는, 테스트 대상 진짜 CalendarEventService
    @InjectMocks
    private CalendarEventService calendarEventService;

    private Member member;
    private Reservation reservation;
    private CalendarEventUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        // 회원 객체 생성 후, id를 리플렉션으로 강제 주입 (1L)
        member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);

        // 캘린더 일정은 예약(Reservation)에 연결되어 있으니, 예약과 그 안의 패키지도 함께 준비
        Package packageInfo = new Package(member, "PREMIUM", 1800000, "코스안내", "2시간", "구성");
        reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );

        // 일정 제목 수정 요청에 필요한 값을 채움
        updateRequest = new CalendarEventUpdateRequest();
        updateRequest.setTitle("김예지 신부님 촬영");
    }

    // 리플렉션으로 엔티티의 private id 필드에 강제로 값을 넣는 테스트 전용 헬퍼 메서드
    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void 본인_소유_일정은_정상적으로_제목이_수정된다() throws Exception {
        // given: member(1L)의 예약(reservation)에 연결된 캘린더 일정(10L)을 준비
        CalendarEvent calendarEvent = new CalendarEvent(reservation, "김예지 / 상록아트홀 / 11:00", reservation.getWeddingDate());
        setId(calendarEvent, 10L);
        when(calendarEventRepository.findById(10L)).thenReturn(Optional.of(calendarEvent));

        // when & then: 소유자가 일치하므로 예외 없이 제목 수정이 끝까지 진행되어야 함
        assertThatCode(() -> calendarEventService.updateTitle(1L, 10L, updateRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_일정이면_수정시_예외가_발생한다() {
        // given: 10L로 일정을 조회하면 아무것도 없다고 답하도록 설정
        when(calendarEventRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> calendarEventService.updateTitle(1L, 10L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 일정입니다.");
    }

    @Test
    void 본인_소유가_아닌_일정이면_수정시_예외가_발생한다() throws Exception {
        // given: otherMember(2L)의 예약에 연결된 캘린더 일정(10L)을 준비
        //        -> member(1L)가 이 일정을 수정하려고 시도하는 상황을 만듦
        Member otherMember = new Member("other@wedule.com", "encoded", "다른 스냅", "01099998888");
        setId(otherMember, 2L);

        Package othersPackage = new Package(otherMember, "PREMIUM", 1800000, "코스안내", "2시간", "구성");
        Reservation othersReservation = new Reservation(
                otherMember, othersPackage, "이도현", "최수아", "010-3390-5521",
                LocalDate.of(2026, 8, 22), LocalTime.of(14, 20), "라비돌"
        );
        CalendarEvent othersEvent = new CalendarEvent(othersReservation, "최수아 / 라비돌 / 14:20", othersReservation.getWeddingDate());
        setId(othersEvent, 10L);
        when(calendarEventRepository.findById(10L)).thenReturn(Optional.of(othersEvent));

        // when & then: 실제 소유자(2L)와 요청자(1L)가 달라 거부되어야 함
        assertThatThrownBy(() -> calendarEventService.updateTitle(1L, 10L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 일정입니다.");
    }
}