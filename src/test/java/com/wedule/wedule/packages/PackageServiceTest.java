package com.wedule.wedule.packages;

import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.member.repository.MemberRepository;
import com.wedule.wedule.packages.dto.PackageCreateRequest;
import com.wedule.wedule.packages.dto.PackageUpdateRequest;
import com.wedule.wedule.packages.entity.Package;
import com.wedule.wedule.packages.repository.PackageRepository;
import com.wedule.wedule.packages.service.PackageService;
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

// PackageService의 생성/수정/삭제 로직을 DB 없이 단위 테스트로 검증
@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    // 진짜 DB 대신 사용할 가짜(Mock) Repository들
    @Mock private PackageRepository packageRepository;
    @Mock private MemberRepository memberRepository;

    // 위 가짜 객체들을 자동으로 주입받아 만들어지는, 테스트 대상 진짜 PackageService
    @InjectMocks
    private PackageService packageService;

    // 여러 테스트에서 공통으로 사용할 회원과 요청 객체
    private Member member;
    private PackageCreateRequest createRequest;
    private PackageUpdateRequest updateRequest;

    // 각 테스트 실행 직전마다 매번 새로 호출되어, 공통 테스트 데이터를 준비함
    @BeforeEach
    void setUp() throws Exception {
        // 회원 객체 생성 후, DB가 자동 채번하는 id를 리플렉션으로 강제 주입 (1L)
        member = new Member("test@wedule.com", "encoded", "셀리에 스냅", "01012345678");
        setId(member, 1L);

        // 패키지 생성 요청에 필요한 값들을 채움
        createRequest = new PackageCreateRequest();
        createRequest.setName("PREMIUM");
        createRequest.setPrice(1800000);
        createRequest.setCourseGuide("촬영코스안내");
        createRequest.setShootingTime("2시간");
        createRequest.setComposition("구성내용");

        // 패키지 수정 요청에 필요한 값들을 채움 (기존과 다른 값으로 구성해 "수정됨"을 명확히 함)
        updateRequest = new PackageUpdateRequest();
        updateRequest.setName("STANDARD");
        updateRequest.setPrice(1500000);
        updateRequest.setCourseGuide("수정된 코스안내");
        updateRequest.setShootingTime("1시간 30분");
        updateRequest.setComposition("수정된 구성내용");
    }

    // 리플렉션으로 엔티티의 private id 필드에 강제로 값을 넣는 테스트 전용 헬퍼 메서드
    // (실제 코드에서는 절대 쓰지 않는 방식. Mock 환경에는 진짜 DB가 없어 id가 항상 null이라,
    //  "이미 저장되어 id가 채번된 상태"를 흉내 내기 위한 용도로만 사용)
    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void 정상적인_패키지_생성에_성공한다() {
        // given: 회원이 존재하고, 저장 요청이 들어오면 넘겨받은 객체를 그대로 돌려주도록 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(packageRepository.save(any(Package.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when & then: createPackage를 호출했을 때 예외 없이 끝까지 실행되는지 확인
        assertThatCode(() -> packageService.createPackage(1L, createRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_회원이면_패키지_생성시_예외가_발생한다() {
        // given: 1L로 회원을 조회하면 아무도 없다고 답하도록 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then: 회원이 없으므로 정확한 메시지의 예외가 던져져야 함
        assertThatThrownBy(() -> packageService.createPackage(1L, createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 업체입니다.");
    }

    @Test
    void 본인_소유_패키지는_정상적으로_수정된다() throws Exception {
        // given: member(1L) 소유의 패키지(10L)를 준비
        Package pkg = new Package(member, "PREMIUM", 1800000, "촬영코스안내", "2시간", "구성내용");
        setId(pkg, 10L);
        when(packageRepository.findById(10L)).thenReturn(Optional.of(pkg));

        // when & then: 소유자가 일치하므로 예외 없이 수정이 끝까지 진행되어야 함
        assertThatCode(() -> packageService.updatePackage(1L, 10L, updateRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_패키지면_수정시_예외가_발생한다() {
        // given: 10L로 패키지를 조회하면 아무것도 없다고 답하도록 설정
        when(packageRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> packageService.updatePackage(1L, 10L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 패키지입니다.");
    }

    @Test
    void 본인_소유가_아닌_패키지면_수정시_예외가_발생한다() throws Exception {
        // given: otherMember(2L) 소유의 패키지(10L)를 준비
        //        -> member(1L)가 이 패키지를 수정하려고 시도하는 상황을 만듦
        Member otherMember = new Member("other@wedule.com", "encoded", "다른 스냅", "01099998888");
        setId(otherMember, 2L);

        Package othersPackage = new Package(otherMember, "PREMIUM", 1800000, "촬영코스안내", "2시간", "구성내용");
        setId(othersPackage, 10L);
        when(packageRepository.findById(10L)).thenReturn(Optional.of(othersPackage));

        // when & then: 실제 소유자(2L)와 요청자(1L)가 달라 거부되어야 함
        // -> "존재하지 않는 패키지입니다"로 동일하게 응답해, 남의 패키지가 실존한다는 사실 자체를 숨김
        assertThatThrownBy(() -> packageService.updatePackage(1L, 10L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 패키지입니다.");
    }

    @Test
    void 본인_소유_패키지는_정상적으로_삭제된다() throws Exception {
        // given: member(1L) 소유의 패키지(10L)를 준비
        Package pkg = new Package(member, "PREMIUM", 1800000, "촬영코스안내", "2시간", "구성내용");
        setId(pkg, 10L);
        when(packageRepository.findById(10L)).thenReturn(Optional.of(pkg));

        // when & then: 소유자가 일치하므로 예외 없이 삭제가 진행되어야 함
        assertThatCode(() -> packageService.deletePackage(1L, 10L))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_패키지면_삭제시_예외가_발생한다() {
        // given
        when(packageRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> packageService.deletePackage(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 패키지입니다.");
    }
}