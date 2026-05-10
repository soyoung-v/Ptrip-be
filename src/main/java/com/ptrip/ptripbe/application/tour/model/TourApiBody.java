package com.ptrip.ptripbe.application.tour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 관광 공공 API의 body 영역을 담는 모델
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiBody(
        TourApiItems items
) {
}
