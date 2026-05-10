package com.ptrip.ptripbe.application.tour.model;

// 프론트에서 바로 사용하기 쉬운 관광 정보 응답 DTO
public record TourItemDto(
        String contentId,
        String contentTypeId,
        String title,
        String addr1,
        String addr2,
        String firstImage,
        String firstImage2,
        String mapX,
        String mapY,
        String tel,
        String overview
) {
}
