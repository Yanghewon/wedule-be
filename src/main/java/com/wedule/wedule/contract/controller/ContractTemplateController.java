package com.wedule.wedule.contract.controller;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.contract.dto.request.ContractTemplateRequest;
import com.wedule.wedule.contract.dto.response.ContractTemplateResponse;
import com.wedule.wedule.contract.service.ContractTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract-template")
public class ContractTemplateController {

    private final ContractTemplateService contractTemplateService;

    public ContractTemplateController(ContractTemplateService contractTemplateService) {
        this.contractTemplateService = contractTemplateService;
    }

    @GetMapping
    public ResponseEntity<ContractTemplateResponse> getTemplate(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(contractTemplateService.getTemplate(memberId));
    }

    @PutMapping
    public ResponseEntity<MessageResponse> saveTemplate(
            Authentication authentication,
            @RequestBody ContractTemplateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        contractTemplateService.saveTemplate(memberId, request);
        return ResponseEntity.ok(new MessageResponse("계약서 템플릿이 저장되었습니다."));
    }
}
