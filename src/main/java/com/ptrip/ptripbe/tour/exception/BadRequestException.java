package com.ptrip.ptripbe.tour.exception;

// 잘못된 요청 파라미터를 표현하는 예외
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
