package com.ptrip.ptripbe.tour.controller;

import com.ptrip.ptripbe.common.ApiResponse;
import com.ptrip.ptripbe.tour.dto.TourItemDto;
import com.ptrip.ptripbe.tour.service.TourService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
// 관광 검색과 상세 조회 엔드포인트를 제공
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/search")
    // 키워드 검색 결과를 공통 응답 형식으로 반환
    public ApiResponse<List<TourItemDto>> search(@RequestParam String keyword) {
        return ApiResponse.ok(tourService.search(keyword));
    }

    @GetMapping("/{contentId}")
    // contentId 기준 상세 정보를 공통 응답 형식으로 반환
    public ApiResponse<TourItemDto> getDetail(@PathVariable String contentId) {
        return ApiResponse.ok(tourService.getDetail(contentId));
    }
}
