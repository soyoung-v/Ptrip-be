package com.ptrip.ptripbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 피트립 백엔드 애플리케이션을 시작하는 진입점
public class PtripBeApplication {

    // 스프링 부트 애플리케이션 컨텍스트를 실행
    public static void main(String[] args) {
        SpringApplication.run(PtripBeApplication.class, args);
    }

}
