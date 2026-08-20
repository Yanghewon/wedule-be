package com.wedule.wedule.reservation;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.member.MemberRepository;
import com.wedule.wedule.reservation.dto.ReservationCreateRequest;
import com.wedule.wedule.reservation.dto.ReservationResponse;
import com.wedule.wedule.reservation.dto.ReservationStatusUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
    }

    // memberId: 로그인한 업체의 id (JWT 필터가 인증 정보에 등록해둔 값에서 나중에 꺼내올 예정)
    // request: 예약 생성에 필요한 나머지 정보를 담은 DTO
    @Transactional
    public Long createReservation(Long memberId, ReservationCreateRequest request) {
        // 1. memberId로 실제 Member 엔티티를 조회
        //    (Reservation은 Member 객체를 참조해야 하므로, id만 갖고는 안 되고 실제 엔티티가 필요함)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업체입니다."));

        // 2. 1차 방어: 애플리케이션 레벨에서 같은 날짜/시간 예약이 이미 있는지 미리 확인
        //    대부분의 경우 여기서 걸러지고, 사용자에게 빠르고 친절한 에러 메시지를 줄 수 있음
        if (reservationRepository.existsByMemberIdAndWeddingDateAndWeddingTime(
                memberId, request.getWeddingDate(), request.getWeddingTime())) {
            throw new IllegalArgumentException("해당 날짜/시간에 이미 예약이 존재합니다.");
        }

        // 3. Reservation 객체 생성
        //    DTO에서 필요한 값들을 이름으로 하나씩 꺼내 쓰므로, 순서를 헷갈릴 위험이 없음
        Reservation reservation = new Reservation(
                member,
                request.getGroomName(),
                request.getBrideName(),
                request.getPhone(),
                request.getWeddingDate(),
                request.getWeddingTime(),
                request.getVenueName()
        );

        // 4. 2차 방어: DB의 유일 제약이 최종적으로 막아줌
        //    동시 요청으로 1차 검증을 둘 다 통과했더라도,
        //    save() 시점에 DB가 중복을 거부하면서 DataIntegrityViolationException이 발생함
        try {
            Reservation savedReservation = reservationRepository.save(reservation);
            return savedReservation.getId();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("해당 날짜/시간에 이미 예약이 존재합니다.");
        }
    }

    // 로그인한 회원의 예약 목록 전체 조회
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
    }

    // 예약 단건 조회 (본인 소유인지 검증 포함)
    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long memberId, Long reservationId) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);
        return new ReservationResponse(reservation);
    }

    // 예약 상태 변경
    @Transactional
    public void updateStatus(Long memberId, Long reservationId, ReservationStatusUpdateRequest request) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);
        reservation.changeStatus(request.getStatus());
        // JPA의 변경 감지(dirty checking) 덕분에, 트랜잭션 안에서 엔티티 필드를 바꾸면
        // 별도로 save()를 호출하지 않아도 트랜잭션 종료 시점에 자동으로 UPDATE 쿼리가 나감
        // (다만 지금은 Service 메서드에 @Transactional이 없어서 이 동작을 온전히 믿기는 이르고,
        //  다음 단계에서 @Transactional을 붙이며 이 개념을 제대로 짚을 예정)
    }

    // 예약 삭제
    @Transactional
    public void deleteReservation(Long memberId, Long reservationId) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);
        reservationRepository.delete(reservation);
    }

    // 예약을 조회하면서 "로그인한 회원의 소유가 맞는지"까지 검증하는 공통 로직
    // 여러 메서드(조회, 수정, 삭제)에서 반복되므로 하나로 뽑아둠
    // 존재하지 않는 경우와 소유자가 다른 경우를 같은 메시지로 통일해서,
    // 클라이언트가 "존재는 하는데 내 것이 아니다"라는 정보를 추측하지 못하도록 함
    private Reservation findOwnedReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        return reservation;
    }
}