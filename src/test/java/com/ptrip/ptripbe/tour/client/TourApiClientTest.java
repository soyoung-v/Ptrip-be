package com.ptrip.ptripbe.tour.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptrip.ptripbe.config.TourApiProperties;
import com.ptrip.ptripbe.tour.exception.ExternalApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
// 관광 API 클라이언트의 서비스키 검증 규칙을 확인
class TourApiClientTest {

    @Mock
    private RestClient restClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    // 서비스키가 비어 있으면 외부 호출 전에 명확한 예외를 던진다
    void searchKeywordThrowsWhenServiceKeyIsBlank() {
        TourApiProperties properties = new TourApiProperties(
                "https://apis.data.go.kr/B551011/KorService2",
                "",
                "ETC",
                "Ptrip"
        );
        TourApiClient client = new TourApiClient(restClient, properties, objectMapper);

        assertThatThrownBy(() -> client.searchKeyword("부산"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("TOUR_API_SERVICE_KEY 환경변수가 설정되지 않았습니다.");
    }

    @Test
    // placeholder 문자열이 남아 있으면 URI 생성 전에 설정 오류를 알린다
    void searchKeywordThrowsWhenServiceKeyPlaceholderRemains() {
        TourApiProperties properties = new TourApiProperties(
                "https://apis.data.go.kr/B551011/KorService2",
                "${TOUR_API_SERVICE_KEY}",
                "ETC",
                "Ptrip"
        );
        TourApiClient client = new TourApiClient(restClient, properties, objectMapper);

        assertThatThrownBy(() -> client.searchKeyword("부산"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("tour.api.service-key 설정이 실제 서비스키로 바인딩되지 않았습니다.");
    }
}
