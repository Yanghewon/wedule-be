package com.wedule.wedule.option;

import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import com.wedule.wedule.option.dto.OptionCreateRequest;
import com.wedule.wedule.option.dto.OptionUpdateRequest;
import com.wedule.wedule.option.entity.Option;
import com.wedule.wedule.option.repository.OptionRepository;
import com.wedule.wedule.option.service.OptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// OptionService의 생성/수정/삭제 로직을 DB 없이 단위 테스트로 검증
@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    // 진짜 DB 대신 사용할 가짜(Mock) Repository들
    @Mock private OptionRepository optionRepository;
    @Mock private MemberRepository memberRepository;

    // 위 가짜 객체들을 자동으로 주입받아 만들어지는, 테스트 대상 진짜 OptionService
    @InjectMocks
    private OptionService optionService;

    // 여러 테스트에서 공통으로 사용할 회원과 요청 객체
    private Member member;
    private OptionCreateRequest createRequest;
    private OptionUpdateRequest updateRequest;

    // 각 테스트 실행 직전마다 매번 새로 호출되어, 공통 테스트 데이터를 준비함
    @BeforeEach
    void setUp() throws Exception {
        // 회원 객체 생성 후, DB가 자동 채번하는 id를 리플렉션으로 강제 주입 (1L)
        member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);

        // 옵션 생성 요청에 필요한 값들을 채움
        createRequest = new OptionCreateRequest();
        createRequest.setName("2부 촬영 추가");
        createRequest.setType(OptionType.ADDON);
        createRequest.setPrice(150000);

        // 옵션 수정 요청에 필요한 값들을 채움
        updateRequest = new OptionUpdateRequest();
        updateRequest.setName("블로그 후기 할인");
        updateRequest.setType(OptionType.DISCOUNT);
        updateRequest.setPrice(-50000);
    }

    // 리플렉션으로 엔티티의 private id 필드에 강제로 값을 넣는 테스트 전용 헬퍼 메서드
    // (Mock 환경에는 진짜 DB가 없어 id가 항상 null이라, 이미 저장된 상태를 흉내 내기 위한 용도)
    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void 정상적인_옵션_생성에_성공한다() {
        // given: 회원이 존재하고, 저장 요청이 들어오면 넘겨받은 객체를 그대로 돌려주도록 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(optionRepository.save(any(Option.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when & then: createOption을 호출했을 때 예외 없이 끝까지 실행되는지 확인
        assertThatCode(() -> optionService.createOption(1L, createRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_회원이면_옵션_생성시_예외가_발생한다() {
        // given: 1L로 회원을 조회하면 아무도 없다고 답하도록 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.createOption(1L, createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 업체입니다.");
    }

    @Test
    void 본인_소유_옵션은_정상적으로_수정된다() throws Exception {
        // given: member(1L) 소유의 옵션(10L)을 준비
        Option option = new Option(member, "2부 촬영 추가", OptionType.ADDON, 150000);
        setId(option, 10L);
        when(optionRepository.findById(10L)).thenReturn(Optional.of(option));

        // when & then: 소유자가 일치하므로 예외 없이 수정이 끝까지 진행되어야 함
        assertThatCode(() -> optionService.updateOption(1L, 10L, updateRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_옵션이면_수정시_예외가_발생한다() {
        // given: 10L로 옵션을 조회하면 아무것도 없다고 답하도록 설정
        when(optionRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.updateOption(1L, 10L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 옵션입니다.");
    }

    @Test
    void 본인_소유가_아닌_옵션이면_수정시_예외가_발생한다() throws Exception {
        // given: otherMember(2L) 소유의 옵션(10L)을 준비
        //        -> member(1L)가 이 옵션을 수정하려고 시도하는 상황을 만듦
        Member otherMember = new Member("other@wedule.com", "encoded", "다른 스냅", "01099998888");
        setId(otherMember, 2L);

        Option othersOption = new Option(otherMember, "2부 촬영 추가", OptionType.ADDON, 150000);
        setId(othersOption, 10L);
        when(optionRepository.findById(10L)).thenReturn(Optional.of(othersOption));

        // when & then: 실제 소유자(2L)와 요청자(1L)가 달라 거부되어야 함
        assertThatThrownBy(() -> optionService.updateOption(1L, 10L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 옵션입니다.");
    }

    @Test
    void 본인_소유_옵션은_정상적으로_삭제된다() throws Exception {
        // given: member(1L) 소유의 옵션(10L)을 준비
        Option option = new Option(member, "2부 촬영 추가", OptionType.ADDON, 150000);
        setId(option, 10L);
        when(optionRepository.findById(10L)).thenReturn(Optional.of(option));

        // when & then: 소유자가 일치하므로 예외 없이 삭제가 진행되어야 함
        assertThatCode(() -> optionService.deleteOption(1L, 10L))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_옵션이면_삭제시_예외가_발생한다() {
        // given
        when(optionRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.deleteOption(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 옵션입니다.");
    }
}