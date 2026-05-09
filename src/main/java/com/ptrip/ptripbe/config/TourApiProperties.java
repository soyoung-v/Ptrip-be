package com.ptrip.ptripbe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tour.api")
// 외부 관광 API 호출 설정값을 바인딩
public record TourApiProperties(
        String baseUrl,
        String serviceKey,
        String mobileOs,
        String mobileApp
) {
}
