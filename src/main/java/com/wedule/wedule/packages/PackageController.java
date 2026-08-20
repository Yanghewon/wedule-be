package com.wedule.wedule.packages;

import com.wedule.wedule.common.dto.MessageResponse;
import com.wedule.wedule.packages.dto.PackageCreateRequest;
import com.wedule.wedule.packages.dto.PackageCreateResponse;
import com.wedule.wedule.packages.dto.PackageResponse;
import com.wedule.wedule.packages.dto.PackageUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 패키지 관련 HTTP 요청을 받는 진입점
@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    // POST /api/packages — 패키지 생성
    @PostMapping
    public ResponseEntity<PackageCreateResponse> createPackage(
            Authentication authentication,
            @RequestBody PackageCreateRequest request
    ) {
        // 로그인한 회원 정보는 JWT 필터가 등록해둔 인증 정보에서 꺼내옴

        Long memberId = (Long) authentication.getPrincipal();
        Long packageId = packageService.createPackage(memberId, request);
        return ResponseEntity.ok(new PackageCreateResponse(packageId, "패키지 생성이 완료되었습니다🤍"));
    }
    // GET /api/packages — 내 패키지 목록 조회
    @GetMapping
    public ResponseEntity<List<PackageResponse>> getPackages(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(packageService.getPackages(memberId));
    }

    // DELETE /api/packages/{id} — 패키지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePackage(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        packageService.deletePackage(memberId, id);
        return ResponseEntity.ok(new MessageResponse("패키지가 삭제되었습니다."));
    }

    // PUT /api/packages/{id} — 패키지 수정
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updatePackage(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody PackageUpdateRequest request
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        packageService.updatePackage(memberId, id, request);
        return ResponseEntity.ok(new MessageResponse("패키지가 수정되었습니다."));
    }
}