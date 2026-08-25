package com.wedule.wedule.reservation.service;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.member.MemberRepository;
import com.wedule.wedule.reservation.dto.request.CustomFieldCreateRequest;
import com.wedule.wedule.reservation.dto.request.CustomFieldUpdateRequest;
import com.wedule.wedule.reservation.dto.response.CustomFieldResponse;
import com.wedule.wedule.reservation.entity.CustomField;
import com.wedule.wedule.reservation.repository.CustomFieldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CustomFieldService {

    private final CustomFieldRepository customFieldRepository;
    private final MemberRepository memberRepository;

    public CustomFieldService(CustomFieldRepository customFieldRepository, MemberRepository memberRepository) {
        this.customFieldRepository = customFieldRepository;
        this.memberRepository = memberRepository;
    }

    // 커스텀 항목 등록
    @Transactional
    public Long createCustomField(Long memberId, CustomFieldCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업체입니다."));

        CustomField customField = new CustomField(member, request.getLabel(), request.getDisplayOrder());
        CustomField savedCustomField = customFieldRepository.save(customField);

        return savedCustomField.getId();
    }

    // 로그인한 작가의 커스텀 항목 목록 조회 (표시 순서대로)
    @Transactional(readOnly = true)
    public List<CustomFieldResponse> getCustomFields(Long memberId) {
        return customFieldRepository.findByMemberIdOrderByDisplayOrderAsc(memberId).stream()
                .map(CustomFieldResponse::new)
                .collect(Collectors.toList());
    }

    // 커스텀 항목 수정 (본인 소유인지 검증 포함)
    @Transactional
    public void updateCustomField(Long memberId, Long customFieldId, CustomFieldUpdateRequest request) {
        CustomField customField = findOwnedCustomField(memberId, customFieldId);
        customField.update(request.getLabel(), request.getDisplayOrder());
    }

    // 커스텀 항목 삭제
    @Transactional
    public void deleteCustomField(Long memberId, Long customFieldId) {
        CustomField customField = findOwnedCustomField(memberId, customFieldId);
        customFieldRepository.delete(customField);
    }

    // Package, Option과 동일한 소유권 검증 패턴
    private CustomField findOwnedCustomField(Long memberId, Long customFieldId) {
        CustomField customField = customFieldRepository.findById(customFieldId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));

        if (!customField.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return customField;
    }
}
