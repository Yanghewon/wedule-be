package com.wedule.wedule.packages;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.member.MemberRepository;
import com.wedule.wedule.packages.dto.PackageCreateRequest;
import com.wedule.wedule.packages.dto.PackageResponse;
import com.wedule.wedule.packages.dto.PackageUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final MemberRepository memberRepository;

    public PackageService(PackageRepository packageRepository, MemberRepository memberRepository) {
        this.packageRepository = packageRepository;
        this.memberRepository = memberRepository;
    }

    // 패키지 생성
    @Transactional
    public Long createPackage(Long memberId, PackageCreateRequest request) {
        // 요청한 회원이 실제 존재하는지 확인 후 엔티티로 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업체입니다."));

        Package pkg = new Package(member, request.getName(), request.getPrice(), request.getCourseGuide(), request.getShootingTime(), request.getComposition());
        Package savedPackage = packageRepository.save(pkg);

        return savedPackage.getId();
    }

    // 로그인한 작가의 패키지 목록 조회
    @Transactional(readOnly = true)
    public List<PackageResponse> getPackages(Long memberId) {
        return packageRepository.findByMemberId(memberId).stream()
                .map(PackageResponse::new)
                .collect(Collectors.toList());
    }

    // 패키지 삭제 (본인 소유인지 검증 포함)
    @Transactional
    public void deletePackage(Long memberId, Long packageId) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 패키지입니다."));

        // Reservation과 동일한 소유권 검증 패턴: 남의 패키지는 못 건드리게 막음
        if (!pkg.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 패키지입니다.");
        }

        packageRepository.delete(pkg);
    }

    // 패키지 수정 (본인 소유인지 검증 포함)
    @Transactional
    public void updatePackage(Long memberId, Long packageId, PackageUpdateRequest request) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 패키지입니다."));

        if (!pkg.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 패키지입니다.");
        }

        pkg.update(request.getName(), request.getPrice(), request.getCourseGuide(), request.getShootingTime(), request.getComposition());
    }
}