package com.ptrip.ptripbe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// 프론트 개발 서버에서 백엔드 API를 호출할 수 있도록 CORS를 설정
public class WebConfig implements WebMvcConfigurer {

    @Override
    // 투어 API 경로에 대해 로컬 프론트 요청을 허용
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET")
                .allowedHeaders("*");
    }
}
