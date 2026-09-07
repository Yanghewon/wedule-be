package com.wedule.wedule.contract.controller;

import com.wedule.wedule.contract.dto.request.ContractCreateRequest;
import com.wedule.wedule.contract.dto.response.ContractDetailResponse;
import com.wedule.wedule.contract.dto.response.ContractResponse;
import com.wedule.wedule.contract.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations/{reservationId}/contract")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    // POST /api/reservations/{id}/contract — 계약서 생성/수정
    @PostMapping
    public ResponseEntity<ContractResponse> createOrUpdateContract(
            Authentication authentication,
            @PathVariable Long reservationId,
            @RequestBody ContractCreateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(contractService.createOrUpdateContract(memberId, reservationId, request));
    }

    // GET /api/reservations/{id}/contract — 계약서 요약 조회 (스타일/내용)
    @GetMapping
    public ResponseEntity<ContractResponse> getContract(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(contractService.getContract(memberId, reservationId));
    }

    // GET /api/reservations/{id}/contract/detail — 계약서 화면 구성에 필요한 전체 데이터
    @GetMapping("/detail")
    public ResponseEntity<ContractDetailResponse> getContractDetail(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(contractService.getContractDetail(memberId, reservationId));
    }
}