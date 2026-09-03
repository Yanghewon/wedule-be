package com.wedule.wedule.contract.repository;

import com.wedule.wedule.contract.entity.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long> {
    Optional<ContractTemplate> findByMemberId(Long memberId);
}
