package com.wedule.wedule.reservation.service;

import com.wedule.wedule.member.Member;
import com.wedule.wedule.member.MemberRepository;
import com.wedule.wedule.option.entity.Option;
import com.wedule.wedule.option.repository.OptionRepository;
import com.wedule.wedule.packages.Package;
import com.wedule.wedule.packages.PackageRepository;
import com.wedule.wedule.reservation.dto.ReservationStatus;
import com.wedule.wedule.reservation.dto.request.CustomFieldValueRequest;
import com.wedule.wedule.reservation.dto.response.CustomFieldValueResponse;
import com.wedule.wedule.reservation.entity.*;
import com.wedule.wedule.reservation.dto.request.ReservationCreateRequest;
import com.wedule.wedule.reservation.dto.response.ReservationResponse;
import com.wedule.wedule.reservation.dto.request.ReservationStatusUpdateRequest;
import com.wedule.wedule.reservation.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final PackageRepository packageRepository;
    private final OptionRepository optionRepository;
    private final ReservationOptionRepository reservationOptionRepository;
    private final CustomFieldValueRepository customFieldValueRepository;
    private final CustomFieldRepository customFieldRepository;
    private final CalendarEventRepository calendarEventRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository,
                              PackageRepository packageRepository, OptionRepository optionRepository,
                              ReservationOptionRepository reservationOptionRepository,
                              CustomFieldValueRepository customFieldValueRepository,
                              CustomFieldRepository customFieldRepository,
                              CalendarEventRepository calendarEventRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.packageRepository = packageRepository;
        this.optionRepository = optionRepository;
        this.reservationOptionRepository = reservationOptionRepository;
        this.customFieldValueRepository = customFieldValueRepository;
        this.customFieldRepository = customFieldRepository;
        this.calendarEventRepository = calendarEventRepository;
    }

    @Transactional
    public Long createReservation(Long memberId, ReservationCreateRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업체입니다."));

        // 2. 선택한 패키지 조회
        Package packageInfo = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 패키지입니다."));

        // 3. 1차 방어: 중복 예약 사전 확인
        if (reservationRepository.existsByMemberIdAndWeddingDateAndWeddingTime(
                memberId, request.getWeddingDate(), request.getWeddingTime())) {
            throw new IllegalArgumentException("해당 날짜/시간에 이미 예약이 존재합니다.");
        }

        // 4. Reservation 객체 생성
        Reservation reservation = new Reservation(
                member,
                packageInfo,
                request.getGroomName(),
                request.getBrideName(),
                request.getPhone(),
                request.getWeddingDate(),
                request.getWeddingTime(),
                request.getVenueName()
        );

        // 5. 2차 방어: DB 유일 제약으로 최종 검증하며 저장
        //    savedReservation을 try 밖에서도 쓸 수 있도록 미리 선언
        Reservation savedReservation;
        try {
            savedReservation = reservationRepository.save(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("해당 날짜/시간에 이미 예약이 존재합니다.");
        }

        // 6. 선택한 옵션들을 예약과 연결
        //    optionIds가 null이거나 비어있으면(옵션 선택 안 함) 이 블록은 그냥 건너뜀
        if (request.getOptionIds() != null) {
            for (Long optionId : request.getOptionIds()) {
                Option option = optionRepository.findById(optionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));
                reservationOptionRepository.save(new ReservationOption(savedReservation, option));
            }
        }

        if (request.getCustomFieldValues() != null) {
            for (CustomFieldValueRequest cfvRequest : request.getCustomFieldValues()) {
                CustomField customField = customFieldRepository.findById(cfvRequest.getCustomFieldId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
                customFieldValueRepository.save(
                        new CustomFieldValue(savedReservation, customField, cfvRequest.getValue())
                );
            }
        }

        // 7. 모든 처리가 끝난 후 최종적으로 id 반환
        return savedReservation.getId();
    }

    // 로그인한 회원의 예약 목록 전체 조회
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .map(this::toResponseWithOptions)
                .collect(Collectors.toList());
    }

    // 예약 단건 조회 (본인 소유인지 검증 포함)
    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long memberId, Long reservationId) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);
        return toResponseWithOptions(reservation);
    }

    // Reservation을 ReservationResponse로 변환하면서, 연결된 옵션 이름 목록도 같이 조회해서 채워 넣는 헬퍼 메서드
    private ReservationResponse toResponseWithOptions(Reservation reservation) {
        List<String> optionNames = reservationOptionRepository.findByReservationId(reservation.getId()).stream()
                .map(ro -> ro.getOption().getName())
                .collect(Collectors.toList());

        List<CustomFieldValueResponse> customFieldValues = customFieldValueRepository.findByReservationId(reservation.getId()).stream()
                .map(cfv -> new CustomFieldValueResponse(cfv.getCustomField().getLabel(), cfv.getValue()))
                .collect(Collectors.toList());

        return new ReservationResponse(reservation, optionNames, customFieldValues);
    }

    // 예약 상태 변경
    @Transactional
    public void updateStatus(Long memberId, Long reservationId, ReservationStatusUpdateRequest request) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);
        reservation.changeStatus(request.getStatus());

        // 계약완료 상태로 바뀌는 경우, 캘린더 일정을 자동 생성
        // (이미 일정이 생성되어 있으면 중복 생성하지 않도록 확인)
        if (request.getStatus() == ReservationStatus.CONTRACTED
                && calendarEventRepository.findByReservationId(reservation.getId()).isEmpty()) {

            String title = String.format("%s / %s / %s",
                    reservation.getBrideName(),
                    reservation.getVenueName(),
                    reservation.getWeddingTime().format(DateTimeFormatter.ofPattern("HH:mm")));

            calendarEventRepository.save(
                    new CalendarEvent(reservation, title, reservation.getWeddingDate())
            );
        }
    }

    // 예약 삭제
    @Transactional
    public void deleteReservation(Long memberId, Long reservationId) {
        Reservation reservation = findOwnedReservation(memberId, reservationId);

        // 이 예약을 참조하는 연관 데이터들을 먼저 삭제 (외래키 제약 때문에 순서가 중요함)
        calendarEventRepository.findByReservationId(reservationId)
                .ifPresent(calendarEventRepository::delete);

        reservationOptionRepository.findByReservationId(reservationId)
                .forEach(reservationOptionRepository::delete);

        customFieldValueRepository.findByReservationId(reservationId)
                .forEach(customFieldValueRepository::delete);

        reservationRepository.delete(reservation);
    }

    // 예약을 조회하면서 "로그인한 회원의 소유가 맞는지"까지 검증하는 공통 로직
    private Reservation findOwnedReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        return reservation;
    }
}