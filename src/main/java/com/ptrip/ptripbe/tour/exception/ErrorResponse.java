package com.ptrip.ptripbe.tour.exception;

// 예외 상황에서 반환할 최소 에러 응답 형식
public record ErrorResponse(
        boolean success,
        String message
) {
}
