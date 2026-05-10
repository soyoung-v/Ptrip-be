# Ptrip Backend

한국관광공사 국문 관광정보 API를 연동한 여행지 검색 미니 백엔드 프로젝트입니다.

프론트엔드에서 사용할 수 있도록 관광지 검색 및 상세 조회 API를 제공하며, 외부 공공 API 응답을 가공해 일관된 형태로 반환합니다.

## 프로젝트 소개

Ptrip Backend는 공공데이터포털 한국관광공사 관광정보 API를 활용한 미니 프로젝트입니다.

외부 관광 API를 직접 브라우저에서 호출하지 않고, Spring Boot 서버에서 중간 API 역할을 수행하도록 구성했습니다.

또한 검색 결과를 프론트엔드에서 사용하기 쉽도록 DTO 형태로 가공하고, 간단한 검색 품질 보정 로직과 전역 예외 처리 구조를 적용했습니다.

## 주요 기능

- 관광지 키워드 검색
- 관광 상세 정보 조회
- 외부 API 응답 DTO 가공
- 주소 기반 검색 결과 후처리 필터
- 공통 응답 형식 제공
- 전역 예외 처리
- 환경변수 기반 서비스키 관리

## 기술 스택

- Java 21
- Spring Boot 4
- Gradle
- RestClient
- Jackson ObjectMapper

## 프로젝트 구조

~~~text
src/main/java/com/ptrip/ptripbe
├── application
│   └── tour
│       ├── controller
│       ├── service
│       ├── client
│       └── model
├── common
│   ├── config
│   ├── response
│   └── exception
└── PtripBeApplication.java
~~~

## 환경 변수 설정

프로젝트 실행 전 환경변수를 설정합니다.

~~~bash
export TOUR_API_SERVICE_KEY=발급받은_서비스키
~~~

또는 IntelliJ 실행 환경 변수에 등록합니다.

## 실행 방법

~~~bash
./gradlew bootRun
~~~

기본 실행 주소:

~~~text
http://localhost:8080
~~~

## API 목록

### 1. 관광지 검색

~~~text
GET /api/tours/search?keyword=부산
~~~

응답 예시:

~~~json
{
  "success": true,
  "data": [
    {
      "contentId": "12345",
      "title": "감천문화마을",
      "addr1": "부산광역시 사하구 감내2로",
      "mapX": "129.010",
      "mapY": "35.097"
    }
  ]
}
~~~

### 2. 관광 상세 조회

~~~text
GET /api/tours/{contentId}
~~~

## 공통 응답 형식

성공 응답:

~~~json
{
  "success": true,
  "data": {}
}
~~~

실패 응답:

~~~json
{
  "success": false,
  "message": "에러 메시지"
}
~~~

## 예외 처리

아래 상황에 대해 전역 예외 처리를 적용했습니다.

- keyword 비어 있음 → 400
- 서비스키 누락 → 502
- 외부 API 호출 실패 → 502
- 외부 JSON 파싱 실패 → 502

## 구현 포인트

### 1. 공공 API 연동

한국관광공사 국문 관광정보 API를 RestClient 기반으로 호출합니다.

사용 API:

~~~text
searchKeyword2
detailCommon2
~~~

### 2. DTO 가공

외부 API 응답을 그대로 반환하지 않고, 프론트엔드에서 사용하기 쉬운 DTO 형태로 변환했습니다.

예:

- 관광지명
- 주소
- 이미지
- 좌표
- 전화번호

### 3. 주소 기반 검색 결과 보정

공공 API keyword 검색 결과가 너무 광범위하게 반환되는 문제를 보완하기 위해, 검색 후 주소(addr1, addr2)에 keyword가 포함된 데이터만 유지하도록 후처리 필터를 적용했습니다.

### 4. 환경변수 기반 서비스키 관리

서비스키를 코드에 하드코딩하지 않고 환경변수 기반으로 관리하도록 구성했습니다.

## 수동 테스트 방법

서버 실행 후 아래 주소로 확인할 수 있습니다.

~~~text
http://localhost:8080/api/tours/search?keyword=부산
~~~

~~~text
http://localhost:8080/api/tours/search?keyword=제주
~~~

~~~text
http://localhost:8080/api/tours/{contentId}
~~~

확인 항목:

- 검색 결과 반환 여부
- 공통 응답 형식 유지 여부
- 검색 결과 필터링 여부
- 상세 조회 응답 여부
- 예외 발생 시 에러 메시지 반환 여부

## 주의 사항

- 외부 공공 API 응답 구조에 의존합니다.
- 인터넷 연결이 필요합니다.
- TOUR_API_SERVICE_KEY 환경변수가 필요합니다.
- 현재는 DB/로그인/저장 기능이 없는 미니 API 서버입니다.
- 검색 결과는 주소 문자열 기반으로 후처리됩니다.

## 개선 예정 사항

- 지역 코드 기반 검색
- 검색 결과 정렬 옵션
- 캐싱 적용
- API 문서화
- 배포 환경 구성
