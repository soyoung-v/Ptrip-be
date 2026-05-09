package com.ptrip.ptripbe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "tour.api.service-key=test-key")
// 기본 스프링 컨텍스트가 정상적으로 올라오는지 확인
class PtripBeApplicationTests {

    @Test
    // 필수 빈 설정이 깨지지 않았는지 점검
    void contextLoads() {
    }

}
