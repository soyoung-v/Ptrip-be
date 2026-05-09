package com.ptrip.ptripbe.tour.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// 서비스 예외를 HTTP 응답 코드와 본문으로 변환
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    // 잘못된 요청 예외를 400 응답으로 변환
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(false, exception.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    // 공공데이터 API 오류를 502 응답으로 변환
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(false, exception.getMessage()));
    }
}
