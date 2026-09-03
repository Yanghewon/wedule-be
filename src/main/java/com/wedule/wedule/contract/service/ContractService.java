package com.wedule.wedule.contract.service;

import com.wedule.wedule.contract.dto.request.ContractCreateRequest;
import com.wedule.wedule.contract.dto.response.ContractResponse;
import com.wedule.wedule.contract.entity.Contract;
import com.wedule.wedule.contract.entity.ContractTemplate;
import com.wedule.wedule.contract.repository.ContractRepository;
import com.wedule.wedule.contract.repository.ContractTemplateRepository;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractTemplateRepository contractTemplateRepository;
    private final ReservationRepository reservationRepository;
    private final ContractPdfService contractPdfService;

    public ContractService(ContractRepository contractRepository,
                           ContractTemplateRepository contractTemplateRepository,
                           ReservationRepository reservationRepository,
                           ContractPdfService contractPdfService) {
        this.contractRepository = contractRepository;
        this.contractTemplateRepository = contractTemplateRepository;
        this.reservationRepository = reservationRepository;
        this.contractPdfService = contractPdfService;
    }

    // 계약서 생성 (이미 있으면 내용/스타일 수정, 없으면 새로 생성 - upsert)
    // content를 안 보내면(비어있으면), 작가가 등록해둔 기본 템플릿 내용을 대신 사용
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

    // 계약서 조회
    @Transactional(readOnly = true)
    public ContractResponse getContract(Long memberId, Long reservationId) {
        findOwnedReservation(memberId, reservationId);

        Contract contract = contractRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("생성된 계약서가 없습니다."));

        return new ContractResponse(contract);
    }

    // PDF 바이트 생성 (다운로드용)
    @Transactional(readOnly = true)
    public byte[] generatePdf(Long memberId, Long reservationId) throws Exception {
        findOwnedReservation(memberId, reservationId);

        Contract contract = contractRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("생성된 계약서가 없습니다. 먼저 계약서를 생성해주세요."));

        return contractPdfService.generate(contract);
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