package com.wedule.wedule.reservation;

import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import com.wedule.wedule.option.repository.OptionRepository;
import com.wedule.wedule.packages.Package;
import com.wedule.wedule.packages.PackageRepository;
import com.wedule.wedule.reservation.dto.ReservationStatus;
import com.wedule.wedule.reservation.dto.request.CustomFieldValueRequest;
import com.wedule.wedule.reservation.dto.request.ReservationCreateRequest;
import com.wedule.wedule.reservation.dto.request.ReservationStatusUpdateRequest;
import com.wedule.wedule.reservation.entity.ReservationOption;
import com.wedule.wedule.reservation.entity.CustomFieldValue;
import com.wedule.wedule.reservation.entity.CalendarEvent;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.repository.*;
import com.wedule.wedule.reservation.service.ReservationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ReservationService.createReservation()의 정상/예외 케이스를 검증하는 단위 테스트
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private PackageRepository packageRepository;
    @Mock private OptionRepository optionRepository;
    @Mock private ReservationOptionRepository reservationOptionRepository;
    @Mock private CustomFieldValueRepository customFieldValueRepository;
    @Mock private CustomFieldRepository customFieldRepository;
    @Mock private CalendarEventRepository calendarEventRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Member member;
    private Package packageInfo;
    private ReservationCreateRequest request;

    // 리플렉션으로 엔티티의 private id 필드에 강제로 값을 넣는 테스트 전용 헬퍼 메서드
    // (실제 서비스 코드에는 이런 방식을 쓰지 않음 - 오직 테스트에서 DB 없이 id를 흉내내기 위한 용도)
    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    // 각 테스트가 실행되기 직전에 매번 새로 호출되는 메서드
    // 여러 테스트에서 공통으로 쓰는 기본 데이터를 미리 준비해둠
    @SneakyThrows
    @BeforeEach
    void setUp() {
        member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);

        packageInfo = new Package(member, "PREMIUM", 1800000, "촬영코스안내", "2시간", "구성내용");

        request = new ReservationCreateRequest();
        request.setPackageId(1L);
        request.setGroomName("박주영");
        request.setBrideName("김예지");
        request.setPhone("010-4073-1805");
        request.setWeddingDate(LocalDate.of(2026, 8, 9));
        request.setWeddingTime(LocalTime.of(11, 0));
        request.setVenueName("상록아트홀");
    }



    @Test
    void 정상적인_예약이면_생성에_성공한다() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(packageInfo));
        when(reservationRepository.existsByMemberIdAndWeddingDateAndWeddingTime(
                1L, request.getWeddingDate(), request.getWeddingTime())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when & then: 예외 없이 정상적으로 끝까지 실행되는지 확인
        assertThatCode(() -> reservationService.createReservation(1L, request))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_회원이면_예외가_발생한다() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 업체입니다.");
    }

    @Test
    void 존재하지_않는_패키지면_예외가_발생한다() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 패키지입니다.");
    }

    @Test
    void 같은_날짜_시간에_이미_예약이_있으면_예외가_발생한다() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(packageInfo));
        when(reservationRepository.existsByMemberIdAndWeddingDateAndWeddingTime(
                1L, request.getWeddingDate(), request.getWeddingTime())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 날짜/시간에 이미 예약이 존재합니다.");
    }

    @Test
    void 존재하지_않는_옵션을_선택하면_예외가_발생한다() {
        // given
        request.setOptionIds(List.of(99L)); // 존재하지 않는 옵션 id

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(packageInfo));
        when(reservationRepository.existsByMemberIdAndWeddingDateAndWeddingTime(
                1L, request.getWeddingDate(), request.getWeddingTime())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(optionRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 옵션입니다.");
    }

    @Test
    void 존재하지_않는_커스텀항목_값을_보내면_예외가_발생한다() {
        // given
        CustomFieldValueRequest cfvRequest = new CustomFieldValueRequest();
        cfvRequest.setCustomFieldId(99L); // 존재하지 않는 커스텀 항목 id
        cfvRequest.setValue("12시");
        request.setCustomFieldValues(List.of(cfvRequest));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(packageRepository.findById(1L)).thenReturn(Optional.of(packageInfo));
        when(reservationRepository.existsByMemberIdAndWeddingDateAndWeddingTime(
                1L, request.getWeddingDate(), request.getWeddingTime())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(customFieldRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 항목입니다.");
    }

    @Test
    void 상태를_계약완료로_변경하면_캘린더_일정이_생성된다() {
        // given: 소유자가 일치하는 예약 준비
        Reservation reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        // member의 id는 실제로는 DB가 채번하지만, Mock 환경이라 null일 수 있음
        // -> findOwnedReservation의 소유권 비교를 통과시키기 위해 member.getId()를 그대로 사용
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        // 아직 캘린더 일정이 없는 상태
        when(calendarEventRepository.findByReservationId(reservation.getId())).thenReturn(Optional.empty());

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest();
        request.setStatus(ReservationStatus.CONTRACTED);

        // when
        reservationService.updateStatus(member.getId(), 1L, request);

        // then: 캘린더 일정 저장이 실제로 호출됐는지 확인
        verify(calendarEventRepository).save(any(CalendarEvent.class));
    }

    @Test
    void 상태를_계약완료가_아닌_값으로_변경하면_캘린더_일정이_생성되지_않는다() {
        // given
        Reservation reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest();
        request.setStatus(ReservationStatus.CONSULTING);

        // when
        reservationService.updateStatus(member.getId(), 1L, request);

        // then: 캘린더 관련 메서드가 전혀 호출되지 않았는지 확인
        verify(calendarEventRepository, never()).save(any(CalendarEvent.class));
    }

    @Test
    void 이미_캘린더_일정이_있으면_다시_계약완료로_바꿔도_중복_생성되지_않는다() {
        // given
        Reservation reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // 이미 캘린더 일정이 존재하는 상태를 가정
        CalendarEvent existingEvent = new CalendarEvent(reservation, "기존 일정", reservation.getWeddingDate());
        when(calendarEventRepository.findByReservationId(reservation.getId()))
                .thenReturn(Optional.of(existingEvent));

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest();
        request.setStatus(ReservationStatus.CONTRACTED);

        // when
        reservationService.updateStatus(member.getId(), 1L, request);

        // then: 이미 있으니 추가로 save가 호출되면 안 됨
        verify(calendarEventRepository, never()).save(any(CalendarEvent.class));
    }

    @Test
    void 존재하지_않는_예약이면_상태변경시_예외가_발생한다() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest();
        request.setStatus(ReservationStatus.CONTRACTED);

        // when & then
        assertThatThrownBy(() -> reservationService.updateStatus(member.getId(), 1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }

    @Test
    void 본인_소유가_아닌_예약이면_상태변경시_예외가_발생한다() throws Exception {
        // given: 다른 작가(otherMember)의 예약
        Member otherMember = new Member("other@wedule.com", "encoded", "다른 스냅", "01099998888");
        setId(otherMember, 2L); // member(1L)와 다른 id를 부여

        Reservation reservation = new Reservation(
                otherMember, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationStatusUpdateRequest request = new ReservationStatusUpdateRequest();
        request.setStatus(ReservationStatus.CONTRACTED);

        // when & then
        assertThatThrownBy(() -> reservationService.updateStatus(member.getId(), 1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }

    @Test
    void 정상_삭제시_연관된_캘린더_옵션_커스텀값이_모두_삭제된다() {
        // given
        Reservation reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        CalendarEvent calendarEvent = new CalendarEvent(reservation, "일정", reservation.getWeddingDate());
        when(calendarEventRepository.findByReservationId(1L)).thenReturn(Optional.of(calendarEvent));

        ReservationOption reservationOption = mock(ReservationOption.class);
        when(reservationOptionRepository.findByReservationId(1L)).thenReturn(List.of(reservationOption));

        CustomFieldValue customFieldValue = mock(CustomFieldValue.class);
        when(customFieldValueRepository.findByReservationId(1L)).thenReturn(List.of(customFieldValue));

        // when
        reservationService.deleteReservation(member.getId(), 1L);

        // then: 각 연관 데이터의 delete가 실제로 호출됐는지 확인
        verify(calendarEventRepository).delete(calendarEvent);
        verify(reservationOptionRepository).delete(reservationOption);
        verify(customFieldValueRepository).delete(customFieldValue);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    void 연관_데이터가_없는_예약도_예외없이_삭제된다() {
        // given: 캘린더, 옵션, 커스텀값이 전부 비어있는 예약
        Reservation reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(calendarEventRepository.findByReservationId(1L)).thenReturn(Optional.empty());
        when(reservationOptionRepository.findByReservationId(1L)).thenReturn(List.of());
        when(customFieldValueRepository.findByReservationId(1L)).thenReturn(List.of());

        // when & then
        assertThatCode(() -> reservationService.deleteReservation(member.getId(), 1L))
                .doesNotThrowAnyException();

        // 연관 데이터가 없으니, 그쪽 delete는 호출되지 않아야 함
        verify(calendarEventRepository, never()).delete(any(CalendarEvent.class));
        // 예약 자체의 삭제는 반드시 호출되어야 함
        verify(reservationRepository).delete(reservation);
    }

    @Test
    void 존재하지_않는_예약이면_삭제시_예외가_발생한다() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.deleteReservation(member.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }
}
