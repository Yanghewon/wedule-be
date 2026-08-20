package com.wedule.wedule.common.exception;

import com.wedule.wedule.common.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 프로젝트 전체에서 발생하는 예외를 한 곳에서 가로채서 처리하는 클래스
// @RestControllerAdvice: 모든 @RestController에서 던져진 예외를 이 클래스가 대신 받아서 처리하겠다는 선언
// (@ControllerAdvice + @ResponseBody가 합쳐진 것으로, 응답을 JSON으로 자동 변환해줌)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // IllegalArgumentException: 지금까지 "이미 가입된 이메일", "존재하지 않는 예약" 등
    // 클라이언트가 잘못된 값을 보냈을 때 일관되게 던져온 예외 타입
    // 이 예외가 프로젝트 어디서 발생하든, 이 메서드가 대신 잡아서 400 응답으로 변환해줌
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        // e.getMessage(): 예외를 던질 때 넣어둔 메시지("이미 가입된 이메일입니다." 등)를 그대로 꺼내옴
        return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }

    // 위에서 명시적으로 처리하지 않은, 예상 못 한 모든 예외를 마지막에 잡아주는 안전망
    // 이게 없으면 우리가 미처 예상 못 한 에러(예: null 참조 실수 등)가 났을 때
    // 스택 트레이스 같은 내부 정보가 그대로 클라이언트에 노출될 수 있음
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception e) {
        // 클라이언트에게는 내부 구현 정보를 감춘 뭉뚱그린 메시지만 전달
        // (실제 원인은 서버 콘솔/로그에서 확인해야 함 - 지금은 우선 콘솔에 출력해두고,
        //  나중에 로그 관리를 다룰 때 정식 Logger로 교체할 예정)
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new MessageResponse("서버 오류가 발생했습니다."));
    }
}