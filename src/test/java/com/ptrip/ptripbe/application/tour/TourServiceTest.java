package com.ptrip.ptripbe.application.tour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptrip.ptripbe.application.tour.model.TourApiBody;
import com.ptrip.ptripbe.application.tour.model.TourApiContent;
import com.ptrip.ptripbe.application.tour.model.TourApiEnvelope;
import com.ptrip.ptripbe.application.tour.model.TourApiItems;
import com.ptrip.ptripbe.application.tour.model.TourItemDto;
import com.ptrip.ptripbe.application.tour.model.TourSearchGetReq;
import com.ptrip.ptripbe.tour.exception.BadRequestException;
import com.ptrip.ptripbe.tour.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
// 관광 서비스의 검색 예외 처리와 DTO 변환 규칙을 검증
class TourServiceTest {

    @Mock
    private TourApiClient tourApiClient;

    private TourService tourService;
    private ObjectMapper objectMapper;

    @BeforeEach
    // 서비스 테스트에 필요한 목과 JSON 파서를 준비
    void setUp() {
        objectMapper = new ObjectMapper();
        tourService = new TourService(tourApiClient, objectMapper);
    }

    @Test
    // 배열 형태 검색 응답이 DTO 목록으로 바뀌는지 확인
    void searchReturnsMappedList() throws Exception {
        String response = """
                [
                  {
                    "contentid": "1",
                    "contenttypeid": "12",
                    "title": "부산 관광지",
                    "addr1": "부산",
                    "addr2": "해운대구",
                    "firstimage": "img1",
                    "firstimage2": "img2",
                    "mapx": "129.0",
                    "mapy": "35.0",
                    "tel": "051-000-0000"
                  }
                ]
                """;

        given(tourApiClient.searchKeyword("부산"))
                .willReturn(createResponse(response));

        List<TourItemDto> result = tourService.search(createReq("부산"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("부산 관광지");
        assertThat(result.get(0).contentId()).isEqualTo("1");
    }

    @Test
    // 주소에 키워드가 없는 결과는 검색 목록에서 제외한다
    void searchFiltersOutIrrelevantItems() throws Exception {
        String response = """
                [
                  {
                    "contentid": "1",
                    "contenttypeid": "12",
                    "title": "Daegu Modern Alley",
                    "addr1": "Daegu Jung-gu",
                    "addr2": "Gyesan-dong",
                    "firstimage": "img1",
                    "firstimage2": "img2",
                    "mapx": "128.0",
                    "mapy": "35.8",
                    "tel": "053-000-0000"
                  },
                  {
                    "contentid": "2",
                    "contenttypeid": "39",
                    "title": "Busan Local Restaurant",
                    "addr1": "Busan Suyeong-gu",
                    "addr2": "Millak-dong",
                    "firstimage": "img3",
                    "firstimage2": "img4",
                    "mapx": "126.9",
                    "mapy": "37.5",
                    "tel": "02-000-0000"
                  }
                ]
                """;

        given(tourApiClient.searchKeyword("DaEgU"))
                .willReturn(createResponse(response));

        List<TourItemDto> result = tourService.search(createReq("  DaEgU  "));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).contentId()).isEqualTo("1");
        assertThat(result.get(0).title()).isEqualTo("Daegu Modern Alley");
    }

    @Test
    // 제목만 일치하고 주소가 다르면 검색 목록에서 제외한다
    void searchFiltersOutTitleOnlyMatches() throws Exception {
        String response = """
                [
                  {
                    "contentid": "3",
                    "contenttypeid": "39",
                    "title": "Daegu Soup Restaurant",
                    "addr1": "Busan Suyeong-gu",
                    "addr2": "Millak-dong",
                    "firstimage": "img5",
                    "firstimage2": "img6",
                    "mapx": "126.9",
                    "mapy": "37.5",
                    "tel": "02-000-0000"
                  }
                ]
                """;

        given(tourApiClient.searchKeyword("Daegu"))
                .willReturn(createResponse(response));

        assertThat(tourService.search(createReq("Daegu"))).isEmpty();
    }

    @Test
    // 결과가 비어 있으면 예외 대신 빈 목록을 반환해야 한다
    void searchReturnsEmptyListWhenNoItemsExist() throws Exception {
        String response = "\"\"";

        given(tourApiClient.searchKeyword("부산"))
                .willReturn(createResponse(response));

        assertThat(tourService.search(createReq("부산"))).isEmpty();
    }

    @Test
    // 필터링 후 남는 결과가 없으면 빈 목록을 반환해야 한다
    void searchReturnsEmptyListWhenAllItemsAreFilteredOut() throws Exception {
        String response = """
                [
                  {
                    "contentid": "2",
                    "contenttypeid": "39",
                    "title": "Busan Local Restaurant",
                    "addr1": "Busan Suyeong-gu",
                    "addr2": "Millak-dong",
                    "firstimage": "img3",
                    "firstimage2": "img4",
                    "mapx": "126.9",
                    "mapy": "37.5",
                    "tel": "02-000-0000"
                  }
                ]
                """;

        given(tourApiClient.searchKeyword("Daegu"))
                .willReturn(createResponse(response));

        assertThat(tourService.search(createReq("Daegu"))).isEmpty();
    }

    @Test
    // 공백 키워드는 서비스 레벨에서 바로 차단한다
    void searchThrows400WhenKeywordIsBlank() {
        assertThatThrownBy(() -> tourService.search(createReq(" ")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("keyword는 비어 있을 수 없습니다.");
    }

    @Test
    // 외부 API 호출 실패는 그대로 상위 계층으로 전달한다
    void searchPropagatesExternalApiFailure() {
        given(tourApiClient.searchKeyword("부산"))
                .willThrow(new ExternalApiException("외부 관광 API 호출에 실패했습니다."));

        assertThatThrownBy(() -> tourService.search(createReq("부산")))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("외부 관광 API 호출에 실패했습니다.");
    }

    private TourSearchGetReq createReq(String keyword) {
        TourSearchGetReq req = new TourSearchGetReq();
        req.setKeyword(keyword);
        return req;
    }

    private TourApiEnvelope createResponse(String itemJson) throws Exception {
        return new TourApiEnvelope(
                new TourApiContent(
                        new TourApiBody(
                                new TourApiItems(objectMapper.readTree(itemJson))
                        )
                )
        );
    }
}
