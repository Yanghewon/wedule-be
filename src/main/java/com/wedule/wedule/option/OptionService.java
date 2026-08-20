package com.wedule.wedule.option;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.member.MemberRepository;
import com.wedule.wedule.option.dto.OptionCreateRequest;
import com.wedule.wedule.option.dto.OptionResponse;
import com.wedule.wedule.option.dto.OptionUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;

    public OptionService(OptionRepository optionRepository, MemberRepository memberRepository) {
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
    }

    // 옵션 생성
    @Transactional
    public Long createOption(Long memberId, OptionCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업체입니다."));

        Option option = new Option(member, request.getName(), request.getType(), request.getPrice());
        Option savedOption = optionRepository.save(option);

        return savedOption.getId();
    }

    // 로그인한 작가의 옵션 목록 조회
    @Transactional(readOnly = true)
    public List<OptionResponse> getOptions(Long memberId) {
        return optionRepository.findByMemberId(memberId).stream()
                .map(OptionResponse::new)
                .collect(Collectors.toList());
    }

    // 옵션 수정 (본인 소유인지 검증 포함)
    @Transactional
    public void updateOption(Long memberId, Long optionId, OptionUpdateRequest request) {
        Option option = findOwnedOption(memberId, optionId);
        option.update(request.getName(), request.getType(), request.getPrice());
        // @Transactional 덕분에 save() 없이도 변경 감지로 자동 UPDATE
    }

    // 옵션 삭제
    @Transactional
    public void deleteOption(Long memberId, Long optionId) {
        Option option = findOwnedOption(memberId, optionId);
        optionRepository.delete(option);
    }

    // Reservation, Package와 동일한 소유권 검증 패턴
    // 존재하지 않는 경우와 소유자가 다른 경우를 같은 메시지로 통일
    private Option findOwnedOption(Long memberId, Long optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));

        if (!option.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 옵션입니다.");
        }

        return option;
    }
}