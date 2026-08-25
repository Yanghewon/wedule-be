package com.wedule.wedule.option.controller;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.option.service.OptionService;
import com.wedule.wedule.option.dto.OptionCreateRequest;
import com.wedule.wedule.option.dto.OptionCreateResponse;
import com.wedule.wedule.option.dto.OptionResponse;
import com.wedule.wedule.option.dto.OptionUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 옵션 관련 HTTP 요청을 받는 진입점
@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    // POST /api/options — 옵션 생성
    @PostMapping
    public ResponseEntity<OptionCreateResponse> createOption(
            Authentication authentication,
            @RequestBody OptionCreateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        Long optionId = optionService.createOption(memberId, request);
        return ResponseEntity.ok(new OptionCreateResponse(optionId, "옵션 생성이 완료되었습니다🤍"));
    }

    // GET /api/options — 내 옵션 목록 조회
    @GetMapping
    public ResponseEntity<List<OptionResponse>> getOptions(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(optionService.getOptions(memberId));
    }

    // PUT /api/options/{id} — 옵션 수정
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateOption(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody OptionUpdateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        optionService.updateOption(memberId, id, request);
        return ResponseEntity.ok(new MessageResponse("옵션이 수정되었습니다."));
    }

    // DELETE /api/options/{id} — 옵션 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteOption(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        optionService.deleteOption(memberId, id);
        return ResponseEntity.ok(new MessageResponse("옵션이 삭제되었습니다."));
    }
}