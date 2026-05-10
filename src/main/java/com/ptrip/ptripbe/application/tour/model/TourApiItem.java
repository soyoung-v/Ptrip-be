package com.ptrip.ptripbe.application.tour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// 관광 공공 API 원본 item 필드를 그대로 매핑하는 모델
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiItem(
        @JsonProperty("contentid") String contentId,
        @JsonProperty("contenttypeid") String contentTypeId,
        @JsonProperty("title") String title,
        @JsonProperty("addr1") String addr1,
        @JsonProperty("addr2") String addr2,
        @JsonProperty("firstimage") String firstImage,
        @JsonProperty("firstimage2") String firstImage2,
        @JsonProperty("mapx") String mapX,
        @JsonProperty("mapy") String mapY,
        @JsonProperty("tel") String tel,
        @JsonProperty("overview") String overview
) {
}
