package com.wedule.wedule.common.exception;

import com.wedule.wedule.common.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

// 프로젝트 전체에서 발생하는 예외를 한 곳에서 가로채서 처리하는 클래스
// @RestControllerAdvice: 모든 @RestController에서 던져진 예외를 이 클래스가 대신 받아서 처리하겠다는 선언
// (@ControllerAdvice + @ResponseBody가 합쳐진 것으로, 응답을 JSON으로 자동 변환해줌)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 이 클래스에서 발생하는 로그를 남기기 위한 Logger
    // 로그 메시지 앞에 자동으로 "GlobalExceptionHandler"라는 클래스 이름이 붙어서,
    // 나중에 로그를 볼 때 "어느 클래스에서 남긴 로그인지" 바로 구분할 수 있음
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // IllegalArgumentException: 지금까지 "이미 가입된 이메일", "존재하지 않는 예약" 등
    // 클라이언트가 잘못된 값을 보냈을 때 일관되게 던져온 예외 타입
    // 이 예외가 프로젝트 어디서 발생하든, 이 메서드가 대신 잡아서 400 응답으로 변환해줌
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        // 사용자의 잘못된 요청으로 인한 것이라 심각한 에러는 아니지만,
        // 어떤 상황에서 이런 요청이 자주 발생하는지 나중에 파악할 수 있도록 warn 레벨로 기록
        log.warn("잘못된 요청 처리: {}", e.getMessage());
        // e.getMessage(): 예외를 던질 때 넣어둔 메시지("이미 가입된 이메일입니다." 등)를 그대로 꺼내옴
        return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }

    // 위에서 명시적으로 처리하지 않은, 예상 못 한 모든 예외를 마지막에 잡아주는 안전망
    // 이게 없으면 우리가 미처 예상 못 한 에러(예: null 참조 실수 등)가 났을 때
    // 스택 트레이스 같은 내부 정보가 그대로 클라이언트에 노출될 수 있음
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception e) {
        // 클라이언트에게는 내부 구현 정보를 감춘 뭉뚱그린 메시지만 전달
        // 실제 원인(전체 스택트레이스 포함)은 error 레벨 로그로 남겨서,
        // 나중에 로그 파일을 통해 정확한 발생 지점과 원인을 추적할 수 있게 함
        log.error("예상하지 못한 서버 오류 발생", e);
        return ResponseEntity.internalServerError().body(new MessageResponse("서버 오류가 발생했습니다."));
    }

    // Validation(@Valid) 검증에 실패했을 때 발생하는 예외를 처리
    // 여러 필드가 동시에 검증 실패할 수 있으므로, 그중 첫 번째 에러 메시지를 응답에 담음
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");
        // 입력값 검증 실패도 사용자 실수에 가까운 상황이라 warn 레벨로 기록
        log.warn("입력값 검증 실패: {}", message);
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }
}