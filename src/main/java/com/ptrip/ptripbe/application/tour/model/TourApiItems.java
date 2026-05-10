package com.ptrip.ptripbe.application.tour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

// 관광 공공 API의 item 영역을 담는 모델
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiItems(
        JsonNode item
) {
}
