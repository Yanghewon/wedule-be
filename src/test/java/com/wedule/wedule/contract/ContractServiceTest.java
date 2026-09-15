package com.wedule.wedule.contract;

import com.wedule.wedule.contract.dto.request.ContractCreateRequest;
import com.wedule.wedule.contract.dto.response.ContractDetailResponse;
import com.wedule.wedule.contract.dto.response.ContractResponse;
import com.wedule.wedule.contract.entity.Contract;
import com.wedule.wedule.contract.entity.ContractStyle;
import com.wedule.wedule.contract.entity.ContractTemplate;
import com.wedule.wedule.contract.repository.ContractRepository;
import com.wedule.wedule.contract.repository.ContractTemplateRepository;
import com.wedule.wedule.contract.service.ContractService;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.option.OptionType;
import com.wedule.wedule.option.entity.Option;
import com.wedule.wedule.packages.entity.Package;
import com.wedule.wedule.payment.entity.Payment;
import com.wedule.wedule.payment.entity.PaymentType;
import com.wedule.wedule.payment.repository.PaymentRepository;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.entity.ReservationOption;
import com.wedule.wedule.reservation.repository.ReservationOptionRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

// ContractService의 생성/수정/조회 로직을 DB 없이 단위 테스트로 검증
@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock private ContractRepository contractRepository;
    @Mock private ContractTemplateRepository contractTemplateRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private ReservationOptionRepository reservationOptionRepository;
    @Mock private PaymentRepository paymentRepository;

    @InjectMocks
    private ContractService contractService;

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
    void 내용을_비워서_보내면_등록된_템플릿_내용으로_자동_채워진다() {
        // given: 아직 계약서가 없고, 작가가 등록해둔 템플릿이 있는 상황
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(contractRepository.findByReservationId(5L)).thenReturn(Optional.empty());
        when(contractTemplateRepository.findByMemberId(1L))
                .thenReturn(Optional.of(new ContractTemplate(member, "제1조 (기본 조항)...")));
        when(contractRepository.save(org.mockito.ArgumentMatchers.any(Contract.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContractCreateRequest request = new ContractCreateRequest();
        request.setStyle(ContractStyle.ELEGANT);
        request.setContent(""); // 내용을 비워서 보냄

        // when
        ContractResponse response = contractService.createOrUpdateContract(1L, 5L, request);

        // then: 빈 값 대신, 템플릿에 등록해둔 내용이 그대로 채워져야 함
        assertThat(response.getContent()).isEqualTo("제1조 (기본 조항)...");
    }

    @Test
    void 내용을_직접_입력하면_템플릿을_무시하고_그_내용을_그대로_사용한다() {
        // given
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(contractRepository.findByReservationId(5L)).thenReturn(Optional.empty());
        when(contractRepository.save(org.mockito.ArgumentMatchers.any(Contract.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContractCreateRequest request = new ContractCreateRequest();
        request.setStyle(ContractStyle.MODERN);
        request.setContent("이 예약만을 위한 특별 조항");

        // when
        ContractResponse response = contractService.createOrUpdateContract(1L, 5L, request);

        // then: 직접 입력한 내용이 그대로 반영되고, 템플릿 조회 자체가 필요 없었는지 확인
        assertThat(response.getContent()).isEqualTo("이 예약만을 위한 특별 조항");
    }

    @Test
    void 이미_계약서가_있으면_새로_만들지_않고_수정한다() {
        // given: 이미 등록된 계약서가 있는 상황
        Contract existingContract = new Contract(reservation, ContractStyle.CLASSIC, "예전 조항");
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(contractRepository.findByReservationId(5L)).thenReturn(Optional.of(existingContract));

        ContractCreateRequest request = new ContractCreateRequest();
        request.setStyle(ContractStyle.WARM);
        request.setContent("수정된 조항");

        // when
        ContractResponse response = contractService.createOrUpdateContract(1L, 5L, request);

        // then: 응답이 새로 만든 게 아니라, 수정된 기존 객체를 기반으로 나왔는지 확인
        assertThat(response.getStyle()).isEqualTo(ContractStyle.WARM);
        assertThat(response.getContent()).isEqualTo("수정된 조항");
    }

    @Test
    void 계약서가_없는_상태에서_조회하면_예외가_발생한다() {
        // given
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(contractRepository.findByReservationId(5L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contractService.getContract(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("생성된 계약서가 없습니다.");
    }

    @Test
    void 계약서_상세조회시_옵션과_결제내역이_함께_포함된다() {
        // given: 계약서, 옵션 1개, 결제 항목 1개가 모두 존재하는 상황
        Contract contract = new Contract(reservation, ContractStyle.ELEGANT, "조항 내용");
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(contractRepository.findByReservationId(5L)).thenReturn(Optional.of(contract));

        Option option = new Option(member, "2부 촬영 추가", OptionType.ADDON, 150000);
        ReservationOption reservationOption = new ReservationOption(reservation, option);
        when(reservationOptionRepository.findByReservationId(5L)).thenReturn(List.of(reservationOption));

        Payment payment = new Payment(reservation, PaymentType.DEPOSIT, 300000);
        when(paymentRepository.findByReservationId(5L)).thenReturn(List.of(payment));

        // when
        ContractDetailResponse detail = contractService.getContractDetail(1L, 5L);

        // then: 예약/업체 정보뿐 아니라, 옵션과 결제 내역까지 정확히 담겨야 함
        assertThat(detail.getBusinessName()).isEqualTo("셀리에 스냅");
        assertThat(detail.getOptions()).hasSize(1);
        assertThat(detail.getOptions().get(0).getName()).isEqualTo("2부 촬영 추가");
        assertThat(detail.getPayments()).hasSize(1);
        assertThat(detail.getPayments().get(0).getAmount()).isEqualTo(300000);
    }
}