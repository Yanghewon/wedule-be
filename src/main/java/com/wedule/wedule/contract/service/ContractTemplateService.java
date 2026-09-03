package com.wedule.wedule.contract.service;

import com.wedule.wedule.contract.dto.request.ContractTemplateRequest;
import com.wedule.wedule.contract.dto.response.ContractTemplateResponse;
import com.wedule.wedule.contract.entity.ContractTemplate;
import com.wedule.wedule.contract.repository.ContractTemplateRepository;
import com.wedule.wedule.member.Member;
import com.wedule.wedule.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractTemplateService {

    private final ContractTemplateRepository contractTemplateRepository;
    private final MemberRepository memberRepository;

    public ContractTemplateService(ContractTemplateRepository contractTemplateRepository,
                                   MemberRepository memberRepository) {
        this.contractTemplateRepository = contractTemplateRepository;
        this.memberRepository = memberRepository;
    }

    // 템플릿 조회 - 아직 한 번도 등록 안 했으면 빈 내용으로 안내
    @Transactional(readOnly = true)
    public ContractTemplateResponse getTemplate(Long memberId) {
        return contractTemplateRepository.findByMemberId(memberId)
                .map(ContractTemplateResponse::new)
                .orElseGet(() -> new ContractTemplateResponse(new ContractTemplate(null, "")));
    }

    // 템플릿 저장 - 이미 있으면 내용만 수정, 없으면 새로 생성 (upsert)
    @Transactional
    public void saveTemplate(Long memberId, ContractTemplateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        contractTemplateRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        template -> template.update(request.getContent()),
                        () -> contractTemplateRepository.save(new ContractTemplate(member, request.getContent()))
                );
    }
}