package com.ptrip.ptripbe.tour.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptrip.ptripbe.config.TourApiProperties;
import com.ptrip.ptripbe.tour.exception.ExternalApiException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
// 한국관광공사 국문 관광정보 API를 직접 호출하는 클라이언트
public class TourApiClient {

    private final RestClient restClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;

    public TourApiClient(RestClient restClient, TourApiProperties properties, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    // 키워드 검색 API에 필요한 파라미터를 붙여 호출
    public JsonNode searchKeyword(String keyword) {
        return get("/searchKeyword2", keyword, null);
    }

    // 상세 조회 API에 필요한 파라미터를 붙여 호출
    public JsonNode getDetail(String contentId) {
        return get("/detailCommon2", null, contentId);
    }

    // 검색과 상세 조회 공통 호출 흐름을 한 곳에서 처리
    private JsonNode get(String path, String keyword, String contentId) {
        String serviceKey = getValidatedServiceKey();

        try {
            String responseBody = restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .path(path)
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("MobileOS", properties.mobileOs())
                                .queryParam("MobileApp", properties.mobileApp())
                                .queryParam("_type", "json");

                        if (keyword != null) {
                            // 검색 API는 요구된 페이징과 정렬 조건을 고정으로 사용
                            builder.queryParam("keyword", keyword)
                                    .queryParam("numOfRows", 12)
                                    .queryParam("pageNo", 1)
                                    .queryParam("arrange", "O");
                        }

                        if (contentId != null) {
                            // 상세 API는 화면에 필요한 기본/이미지/주소/지도/개요 정보를 함께 요청
                            builder.queryParam("contentId", contentId)
                                    .queryParam("defaultYN", "Y")
                                    .queryParam("firstImageYN", "Y")
                                    .queryParam("addrinfoYN", "Y")
                                    .queryParam("mapinfoYN", "Y")
                                    .queryParam("overviewYN", "Y");
                        }

                        return builder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            return objectMapper.readTree(responseBody);
        } catch (RestClientException | JsonProcessingException exception) {
            // 외부 API 오류는 서비스 계층에서 502로 변환할 수 있게 감싼다
            throw new ExternalApiException("외부 관광 API 호출에 실패했습니다.", exception);
        }
    }

    // 환경변수 미설정이나 placeholder 원문이 남은 경우를 요청 전에 차단
    private String getValidatedServiceKey() {
        String serviceKey = properties.serviceKey();

        if (!StringUtils.hasText(serviceKey)) {
            throw new ExternalApiException("TOUR_API_SERVICE_KEY 환경변수가 설정되지 않았습니다.");
        }

        if (serviceKey.contains("${") || serviceKey.contains("}")) {
            throw new ExternalApiException("tour.api.service-key 설정이 실제 서비스키로 바인딩되지 않았습니다.");
        }

        return serviceKey;
    }
}
