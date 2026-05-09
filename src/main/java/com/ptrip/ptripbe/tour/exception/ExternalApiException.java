package com.ptrip.ptripbe.tour.exception;

// 외부 관광 API 호출 실패를 표현하는 예외
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
