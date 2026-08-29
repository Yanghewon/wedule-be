package com.wedule.wedule.reservation.controller;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.reservation.dto.request.CustomFieldCreateRequest;
import com.wedule.wedule.reservation.dto.request.CustomFieldUpdateRequest;
import com.wedule.wedule.reservation.dto.response.CustomFieldResponse;
import com.wedule.wedule.reservation.service.CustomFieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 커스텀 항목 관련 HTTP 요청을 받는 진입점
@RestController
@RequestMapping("/api/custom-fields")
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    public CustomFieldController(CustomFieldService customFieldService) {
        this.customFieldService = customFieldService;
    }

    // POST /api/custom-fields — 커스텀 항목 등록
    @PostMapping
    public ResponseEntity<MessageResponse> createCustomField(
            Authentication authentication,
            @RequestBody CustomFieldCreateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        customFieldService.createCustomField(memberId, request);
        return ResponseEntity.ok(new MessageResponse("항목이 등록되었습니다🤍"));
    }

    // GET /api/custom-fields — 내 커스텀 항목 목록 조회
    @GetMapping
    public ResponseEntity<List<CustomFieldResponse>> getCustomFields(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(customFieldService.getCustomFields(memberId));
    }

    // PUT /api/custom-fields/{id} — 커스텀 항목 수정
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateCustomField(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody CustomFieldUpdateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        customFieldService.updateCustomField(memberId, id, request);
        return ResponseEntity.ok(new MessageResponse("항목이 수정되었습니다🤍"));
    }

    // DELETE /api/custom-fields/{id} — 커스텀 항목 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCustomField(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        customFieldService.deleteCustomField(memberId, id);
        return ResponseEntity.ok(new MessageResponse("항목이 삭제되었습니다."));
    }
}