package com.wedule.wedule.contract.service;

import com.wedule.wedule.contract.dto.request.ContractCreateRequest;
import com.wedule.wedule.contract.dto.response.ContractDetailResponse;
import com.wedule.wedule.contract.dto.response.ContractResponse;
import com.wedule.wedule.contract.entity.Contract;
import com.wedule.wedule.contract.entity.ContractTemplate;
import com.wedule.wedule.contract.repository.ContractRepository;
import com.wedule.wedule.contract.repository.ContractTemplateRepository;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.payment.repository.PaymentRepository;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.entity.ReservationOption;
import com.wedule.wedule.reservation.repository.ReservationOptionRepository;
import com.wedule.wedule.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractTemplateRepository contractTemplateRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationOptionRepository reservationOptionRepository;
    private final PaymentRepository paymentRepository;

    public ContractService(ContractRepository contractRepository,
                           ContractTemplateRepository contractTemplateRepository,
                           ReservationRepository reservationRepository,
                           ReservationOptionRepository reservationOptionRepository,
                           PaymentRepository paymentRepository) {
        this.contractRepository = contractRepository;
        this.contractTemplateRepository = contractTemplateRepository;
        this.reservationRepository = reservationRepository;
        this.reservationOptionRepository = reservationOptionRepository;
        this.paymentRepository = paymentRepository;
    }

    // 계약서 생성/수정 (upsert)
    @Transactional
    public ContractResponse createOrUpdateContract(Long memberId, Long reservationId, ContractCreateRequest request) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);

        String content = request.getContent();
        if (content == null || content.isBlank()) {
            content = contractTemplateRepository.findByMemberId(memberId)
                    .map(ContractTemplate::getContent)
                    .orElse("");
        }
        String finalContent = content;

        Contract contract = contractRepository.findByReservationId(reservationId)
                .map(existing -> {
                    existing.update(request.getStyle(), finalContent);
                    return existing;
                })
                .orElseGet(() -> contractRepository.save(new Contract(reservation, request.getStyle(), finalContent)));

        return new ContractResponse(contract);
    }

    // 계약서 요약 조회 (스타일/내용만)
    @Transactional(readOnly = true)
    public ContractResponse getContract(Long memberId, Long reservationId) {
        findOwnedReservation(memberId, reservationId);

        Contract contract = contractRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("생성된 계약서가 없습니다."));

        return new ContractResponse(contract);
    }

    // 계약서를 화면에 그리는 데 필요한 모든 데이터를 모아서 반환 (프론트가 이 데이터로 디자인)
    @Transactional(readOnly = true)
    public ContractDetailResponse getContractDetail(Long memberId, Long reservationId) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);
        Member member = reservation.getMember();

        Contract contract = contractRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("생성된 계약서가 없습니다. 먼저 계약서를 생성해주세요."));

        List<ContractDetailResponse.OptionLine> options = reservationOptionRepository.findByReservationId(reservationId).stream()
                .map(ReservationOption::getOption)
                .map(option -> new ContractDetailResponse.OptionLine(option.getName(), option.getPrice()))
                .collect(Collectors.toList());

        List<ContractDetailResponse.PaymentLine> payments = paymentRepository.findByReservationId(reservationId).stream()
                .map(payment -> new ContractDetailResponse.PaymentLine(
                        payment.getType().name(), payment.getAmount(), payment.isPaid()))
                .collect(Collectors.toList());

        return new ContractDetailResponse(contract, reservation, member, options, payments);
    }

    private Reservation findOwnedReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        return reservation;
    }
}