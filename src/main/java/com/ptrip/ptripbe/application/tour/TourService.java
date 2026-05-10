package com.ptrip.ptripbe.application.tour;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptrip.ptripbe.application.tour.model.TourApiEnvelope;
import com.ptrip.ptripbe.application.tour.model.TourApiItem;
import com.ptrip.ptripbe.application.tour.model.TourItemDto;
import com.ptrip.ptripbe.application.tour.model.TourSearchGetReq;
import com.ptrip.ptripbe.tour.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
// 관광 검색/상세 응답을 서비스 규칙에 맞게 가공
public class TourService {

    private final TourApiClient tourApiClient;
    private final ObjectMapper objectMapper;

    public TourService(TourApiClient tourApiClient, ObjectMapper objectMapper) {
        this.tourApiClient = tourApiClient;
        this.objectMapper = objectMapper;
    }

    // 키워드 검색 결과를 프론트에서 쓰기 쉬운 DTO 목록으로 변환
    public List<TourItemDto> search(TourSearchGetReq req) {
        if (!StringUtils.hasText(req.getKeyword())) {
            throw new BadRequestException("keyword는 비어 있을 수 없습니다.");
        }

        String keyword = req.getKeyword().trim();
        TourApiEnvelope response = tourApiClient.searchKeyword(keyword);
        return extractItems(response).stream()
                // 지역 키워드와 무관한 상호명 결과를 한 번 더 걸러낸다
                .filter(item -> matchesKeyword(item, keyword))
                .map(this::toDto)
                .toList();
    }

    // 상세 조회 결과에서 첫 번째 관광 정보만 DTO로 변환
    public TourItemDto getDetail(String contentId) {
        List<TourApiItem> items = extractItems(tourApiClient.getDetail(contentId));
        if (items.isEmpty()) {
            return null;
        }

        return toDto(items.get(0));
    }

    // 공공데이터 API 필드명을 프론트 DTO 필드로 매핑
    private TourItemDto toDto(TourApiItem item) {
        return new TourItemDto(
                item.contentId(),
                item.contentTypeId(),
                item.title(),
                item.addr1(),
                item.addr2(),
                item.firstImage(),
                item.firstImage2(),
                item.mapX(),
                item.mapY(),
                item.tel(),
                item.overview()
        );
    }

    // 외부 응답의 item 노드를 배열과 단건 객체 모두 동일한 목록 형태로 정리
    private List<TourApiItem> extractItems(TourApiEnvelope response) {
        if (response == null
                || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null) {
            return List.of();
        }

        JsonNode itemNode = response.response().body().items().item();
        if (itemNode == null || itemNode.isNull() || itemNode.isMissingNode()) {
            return List.of();
        }

        if (itemNode.isTextual() && itemNode.asText().isBlank()) {
            return List.of();
        }

        if (itemNode.isArray()) {
            return objectMapper.convertValue(itemNode, new TypeReference<>() {});
        }

        return List.of(objectMapper.convertValue(itemNode, TourApiItem.class));
    }

    // 주소에 검색어가 포함된 결과만 유지한다
    private boolean matchesKeyword(TourApiItem item, String keyword) {
        String normalizedKeyword = normalize(keyword);

        return contains(item.addr1(), normalizedKeyword)
                || contains(item.addr2(), normalizedKeyword);
    }

    // 공백과 대소문자 차이를 없애고 비교할 문자열을 정리한다
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    // null 안전하게 포함 여부를 검사한다
    private boolean contains(String source, String keyword) {
        return normalize(source).contains(keyword);
    }
}
