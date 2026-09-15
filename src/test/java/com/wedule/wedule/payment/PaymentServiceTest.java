package com.wedule.wedule.payment;

import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.packages.entity.Package;
import com.wedule.wedule.payment.dto.request.PaymentCreateRequest;
import com.wedule.wedule.payment.dto.request.PaymentMarkPaidRequest;
import com.wedule.wedule.payment.dto.response.PaymentSummaryResponse;
import com.wedule.wedule.payment.entity.Payment;
import com.wedule.wedule.payment.entity.PaymentType;
import com.wedule.wedule.payment.repository.PaymentRepository;
import com.wedule.wedule.payment.service.PaymentService;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.repository.ReservationRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// PaymentService의 등록/요약계산/입금처리 로직을 DB 없이 단위 테스트로 검증
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Member member;
    private Reservation reservation;

    @BeforeEach
    void setUp() throws Exception {
        member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);

        Package packageInfo = new Package(member, "PREMIUM", 1800000, "코스안내", "2시간", "구성");
        reservation = new Reservation(
                member, packageInfo, "박주영", "김예지", "010-4073-1805",
                LocalDate.of(2026, 8, 9), LocalTime.of(11, 0), "상록아트홀"
        );
        setId(reservation, 5L);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void 결제_항목_등록에_성공한다() {
        // given: 본인 소유 예약이 존재하고, 저장 요청이 들어오면 넘겨받은 객체를 그대로 돌려주도록 설정
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setType(PaymentType.DEPOSIT);
        request.setAmount(300000);

        // when & then
        assertThatCode(() -> paymentService.createPayment(1L, 5L, request))
                .doesNotThrowAnyException();
    }

    @Test
    void 결제_요약_조회시_금액이_정확히_합산된다() throws Exception {
        // given: 계약금(입금완료 30만원), 잔금(미입금 120만원) 두 건이 등록된 상황
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        Payment deposit = new Payment(reservation, PaymentType.DEPOSIT, 300000);
        deposit.markAsPaid(LocalDate.of(2026, 6, 1)); // 입금 완료 처리
        setId(deposit, 100L);

        Payment balance = new Payment(reservation, PaymentType.BALANCE, 1200000);
        // 잔금은 입금 처리를 안 해서 isPaid = false 상태 그대로
        setId(balance, 101L);

        when(paymentRepository.findByReservationId(5L)).thenReturn(List.of(deposit, balance));

        // when
        PaymentSummaryResponse summary = paymentService.getPaymentSummary(1L, 5L);

        // then: 총액 150만원, 입금액 30만원, 미수금 120만원, 아직 완납은 아님
        assertThat(summary.getTotalAmount()).isEqualTo(1500000);
        assertThat(summary.getPaidAmount()).isEqualTo(300000);
        assertThat(summary.getUnpaidAmount()).isEqualTo(1200000);
        assertThat(summary.isFullyPaid()).isFalse();
    }

    @Test
    void 모든_결제항목이_입금완료되면_완납으로_계산된다() throws Exception {
        // given: 계약금, 잔금 둘 다 입금 완료된 상황
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        Payment deposit = new Payment(reservation, PaymentType.DEPOSIT, 300000);
        deposit.markAsPaid(LocalDate.of(2026, 6, 1));
        setId(deposit, 100L);

        Payment balance = new Payment(reservation, PaymentType.BALANCE, 1200000);
        balance.markAsPaid(LocalDate.of(2026, 6, 15));
        setId(balance, 101L);

        when(paymentRepository.findByReservationId(5L)).thenReturn(List.of(deposit, balance));

        // when
        PaymentSummaryResponse summary = paymentService.getPaymentSummary(1L, 5L);

        // then: 미수금 0원, 완납 상태
        assertThat(summary.getUnpaidAmount()).isEqualTo(0);
        assertThat(summary.isFullyPaid()).isTrue();
    }

    @Test
    void 결제_항목이_하나도_없으면_완납이_아니라고_계산된다() {
        // given: 아직 결제 항목을 하나도 등록 안 한 예약
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.findByReservationId(5L)).thenReturn(List.of());

        // when
        PaymentSummaryResponse summary = paymentService.getPaymentSummary(1L, 5L);

        // then: 받을 돈 자체가 등록되지 않은 상태라, "완납"이 아니라 "미등록" 상태여야 함
        assertThat(summary.getTotalAmount()).isEqualTo(0);
        assertThat(summary.isFullyPaid()).isFalse();
    }

    @Test
    void 환불_금액은_음수로_반영되어_총액에서_차감된다() throws Exception {
        // given: 계약금 30만원(입금완료) + 환불 5만원(음수 금액으로 등록, 입금완료 처리)
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        Payment deposit = new Payment(reservation, PaymentType.DEPOSIT, 300000);
        deposit.markAsPaid(LocalDate.of(2026, 6, 1));
        setId(deposit, 100L);

        Payment refund = new Payment(reservation, PaymentType.REFUND, -50000);
        refund.markAsPaid(LocalDate.of(2026, 6, 20));
        setId(refund, 102L);

        when(paymentRepository.findByReservationId(5L)).thenReturn(List.of(deposit, refund));

        // when
        PaymentSummaryResponse summary = paymentService.getPaymentSummary(1L, 5L);

        // then: 총액 = 30만 - 5만 = 25만원, 입금액도 같은 방식으로 25만원 (환불도 "처리된 금액"이라 paidAmount에 포함)
        assertThat(summary.getTotalAmount()).isEqualTo(250000);
        assertThat(summary.getPaidAmount()).isEqualTo(250000);
    }

    @Test
    void 입금_체크시_입금여부와_날짜가_기록된다() throws Exception {
        // given
        Payment payment = new Payment(reservation, PaymentType.DEPOSIT, 300000);
        setId(payment, 100L);
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        PaymentMarkPaidRequest request = new PaymentMarkPaidRequest();
        request.setPaidDate(LocalDate.of(2026, 6, 1));

        // when
        paymentService.markAsPaid(1L, 5L, 100L, request);

        // then: 엔티티 자체의 상태가 실제로 바뀌었는지 확인
        assertThat(payment.isPaid()).isTrue();
        assertThat(payment.getPaidDate()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    void 존재하지_않는_예약에_결제_등록시_예외가_발생한다() {
        // given
        when(reservationRepository.findById(5L)).thenReturn(Optional.empty());

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setType(PaymentType.DEPOSIT);
        request.setAmount(300000);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(1L, 5L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 예약입니다.");
    }
}