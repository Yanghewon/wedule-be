package com.wedule.wedule.contract.controller;

import com.wedule.wedule.contract.dto.request.ContractCreateRequest;
import com.wedule.wedule.contract.dto.response.ContractResponse;
import com.wedule.wedule.contract.service.ContractService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    // GET /api/reservations/{id}/contract — 계약서 정보(내용/스타일) 조회
    @GetMapping
    public ResponseEntity<ContractResponse> getContract(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(contractService.getContract(memberId, reservationId));
    }

    // GET /api/reservations/{id}/contract/download — 실제 PDF 파일 다운로드
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadContract(
            Authentication authentication,
            @PathVariable Long reservationId
    ) throws Exception {
        Long memberId = (Long) authentication.getPrincipal();
        byte[] pdfBytes = contractService.generatePdf(memberId, reservationId);

        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("contract.pdf").build().toString())
                .contentLength(pdfBytes.length)
                .body(resource);
    }
}