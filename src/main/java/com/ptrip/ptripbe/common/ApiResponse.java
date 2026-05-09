package com.ptrip.ptripbe.common;

// API 응답을 success/data 형태로 감싸는 공통 래퍼
public record ApiResponse<T>(
        boolean success,
        T data
) {

    // 성공 응답을 일관된 형식으로 생성
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data);
    }
}
