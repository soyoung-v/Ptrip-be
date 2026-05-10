package com.ptrip.ptripbe.application.tour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 관광 공공 API 최상위 응답 구조를 담는 모델
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiEnvelope(
        TourApiContent response
) {
}
