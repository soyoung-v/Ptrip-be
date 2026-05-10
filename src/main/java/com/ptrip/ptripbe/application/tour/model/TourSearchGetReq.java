package com.ptrip.ptripbe.application.tour.model;

// 검색 쿼리 파라미터를 묶어 받는 요청 모델
public class TourSearchGetReq {
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
