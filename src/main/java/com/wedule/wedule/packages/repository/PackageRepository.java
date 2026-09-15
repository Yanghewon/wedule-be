package com.wedule.wedule.packages.repository;

import com.wedule.wedule.packages.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<com.wedule.wedule.packages.entity.Package, Long> {

    // 특정 작가가 등록한 패키지 목록만 조회
    List<Package> findByMemberId(Long memberId);
}