package com.ptrip.ptripbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TourApiProperties.class)
// 외부 관광 API 호출에 필요한 공통 빈을 등록
public class AppConfig {

    @Bean
    // 외부 API 호출 시 재사용할 RestClient 빌더를 생성
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    // 외부 API JSON 응답을 수동 파싱할 ObjectMapper를 생성
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    // 설정된 관광 API 기본 주소로 RestClient를 구성
    public RestClient restClient(RestClient.Builder builder, TourApiProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }
}
