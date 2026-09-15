package com.wedule.wedule.contract;

import com.wedule.wedule.contract.dto.request.ContractTemplateRequest;
import com.wedule.wedule.contract.dto.response.ContractTemplateResponse;
import com.wedule.wedule.contract.entity.ContractTemplate;
import com.wedule.wedule.contract.repository.ContractTemplateRepository;
import com.wedule.wedule.contract.service.ContractTemplateService;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ContractTemplateService의 조회/저장(upsert) 로직을 DB 없이 단위 테스트로 검증
@ExtendWith(MockitoExtension.class)
class ContractTemplateServiceTest {

    @Mock private ContractTemplateRepository contractTemplateRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private ContractTemplateService contractTemplateService;

    private Member member;

    @BeforeEach
    void setUp() throws Exception {
        member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void 등록한_템플릿이_있으면_그_내용을_조회한다() {
        // given: 이미 등록해둔 템플릿이 있는 상황
        ContractTemplate template = new ContractTemplate(member, "제1조 (촬영 목적)...");
        when(contractTemplateRepository.findByMemberId(1L)).thenReturn(Optional.of(template));

        // when
        ContractTemplateResponse response = contractTemplateService.getTemplate(1L);

        // then: 등록해둔 내용이 그대로 응답에 담겨야 함
        assertThat(response.getContent()).isEqualTo("제1조 (촬영 목적)...");
    }

    @Test
    void 등록한_템플릿이_없으면_빈_내용으로_응답한다() {
        // given: 아직 템플릿을 한 번도 등록 안 한 상황
        when(contractTemplateRepository.findByMemberId(1L)).thenReturn(Optional.empty());

        // when
        ContractTemplateResponse response = contractTemplateService.getTemplate(1L);

        // then: 예외 없이, 빈 문자열로 안전하게 응답해야 함
        assertThat(response.getContent()).isEmpty();
    }

    @Test
    void 기존_템플릿이_있으면_새로_만들지_않고_내용만_수정한다() {
        // given: 이미 등록된 템플릿이 있는 상황
        ContractTemplate existingTemplate = new ContractTemplate(member, "예전 내용");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(contractTemplateRepository.findByMemberId(1L)).thenReturn(Optional.of(existingTemplate));

        ContractTemplateRequest request = new ContractTemplateRequest();
        request.setContent("새로운 내용");

        // when
        contractTemplateService.saveTemplate(1L, request);

        // then: 기존 객체의 내용이 실제로 바뀌었는지 확인
        assertThat(existingTemplate.getContent()).isEqualTo("새로운 내용");
        // 그리고 새로 저장(save)하는 쪽은 절대 호출되지 않아야 함 (수정이지 생성이 아니므로)
        verify(contractTemplateRepository, never()).save(any(ContractTemplate.class));
    }

    @Test
    void 기존_템플릿이_없으면_새로_생성한다() {
        // given: 아직 템플릿이 없는 상황
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(contractTemplateRepository.findByMemberId(1L)).thenReturn(Optional.empty());

        ContractTemplateRequest request = new ContractTemplateRequest();
        request.setContent("처음 등록하는 내용");

        // when
        contractTemplateService.saveTemplate(1L, request);

        // then: 없었으니, 새로 저장(save)하는 쪽이 실제로 호출되어야 함
        verify(contractTemplateRepository).save(any(ContractTemplate.class));
    }

    @Test
    void 존재하지_않는_회원이면_템플릿_저장시_예외가_발생한다() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        ContractTemplateRequest request = new ContractTemplateRequest();
        request.setContent("내용");

        // when & then
        assertThatThrownBy(() -> contractTemplateService.saveTemplate(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}