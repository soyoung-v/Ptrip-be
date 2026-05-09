package com.ptrip.ptripbe.tour.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ptrip.ptripbe.tour.client.TourApiClient;
import com.ptrip.ptripbe.tour.dto.TourItemDto;
import com.ptrip.ptripbe.tour.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
// 관광 검색/상세 응답을 서비스 규칙에 맞게 가공
public class TourService {

    private final TourApiClient tourApiClient;

    public TourService(TourApiClient tourApiClient) {
        this.tourApiClient = tourApiClient;
    }

    // 키워드 검색 결과를 프론트에서 쓰기 쉬운 DTO 목록으로 변환
    public List<TourItemDto> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new BadRequestException("keyword는 비어 있을 수 없습니다.");
        }

        JsonNode response = tourApiClient.searchKeyword(keyword.trim());
        JsonNode itemsNode = response.path("response").path("body").path("items").path("item");

        if (itemsNode.isMissingNode() || itemsNode.isNull()) {
            return List.of();
        }

        List<TourItemDto> items = new ArrayList<>();

        if (itemsNode.isArray()) {
            itemsNode.forEach(item -> items.add(toDto(item)));
            return items;
        }

        // 결과가 단건 객체로 올 수 있어 배열이 아니어도 동일하게 처리
        items.add(toDto(itemsNode));
        return items;
    }

    // 상세 조회 결과에서 첫 번째 관광 정보만 DTO로 변환
    public TourItemDto getDetail(String contentId) {
        JsonNode response = tourApiClient.getDetail(contentId);
        JsonNode itemNode = response.path("response").path("body").path("items").path("item");

        if (itemNode.isArray()) {
            if (itemNode.isEmpty()) {
                return null;
            }
            return toDto(itemNode.get(0));
        }

        if (itemNode.isMissingNode() || itemNode.isNull()) {
            return null;
        }

        return toDto(itemNode);
    }

    // 공공데이터 API 필드명을 프론트 DTO 필드로 매핑
    private TourItemDto toDto(JsonNode item) {
        return new TourItemDto(
                text(item, "contentid"),
                text(item, "contenttypeid"),
                text(item, "title"),
                text(item, "addr1"),
                text(item, "addr2"),
                text(item, "firstimage"),
                text(item, "firstimage2"),
                text(item, "mapx"),
                text(item, "mapy"),
                text(item, "tel"),
                text(item, "overview")
        );
    }

    // 누락된 값은 null로 정리해 프론트 처리 부담을 줄인다
    private String text(JsonNode item, String fieldName) {
        JsonNode value = item.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }
}
