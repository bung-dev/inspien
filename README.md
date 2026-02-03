# Inspien Integration Service (EAI Assignment)

온라인 쇼핑몰 주문 데이터를 연계 처리하는 Spring Boot 기반 서비스입니다.
주문 생성 API를 통해 주문을 적재하고, 회계용 파일을 생성해 SFTP로 전송하며,
5분 주기 배치로 운송사 DB로 전달합니다.

## 아키텍처
![아키텍처](https://github.com/user-attachments/assets/434f4ccd-de4d-4ac3-ba38-d342dfe9674a)


## 주요 기능
- 주문 API 수신 (Base64 + EUC-KR XML)
- XML 파싱/검증/매핑 후 주문 DB 적재
- 영수증 포맷 파일 생성 및 SFTP 전송
- 5분 주기 배치로 SHIPMENT_TB 적재 및 주문 상태 업데이트
- traceId 기반 로깅 (로컬 로그 파일)

## 설정 파일
- `src/main/resources/application.properties` (공통 + 기본 프로파일)
- `src/main/resources/application-local.properties` (로컬 MySQL + SFTP)
- `src/main/resources/application-prod.properties` (과제 Oracle + SFTP)

주요 프로퍼티:
- `application.key`: 지원자 키
- `spring.datasource.*`: DB 접속 정보
- `sftp.*`: SFTP 접속 정보

## API
### POST /api/order
요청 본문:
```json
{
  "base64Xml": "BASE64_ENCODED_EUC_KR_XML"
}
```
응답: 주문 적재 결과(JSON)

## 배치 스케줄
- `ShipmentScheduler`: 5분 주기
- `ShipmentService`: 미전송 주문 조회 → SHIPMENT_TB 적재 → ORDER_TB 상태 업데이트

## 트랜잭션 처리
- 주문 적재 + 파일 생성 + SFTP 전송을 동일 트랜잭션 단위로 처리
- SFTP 전송 실패 시 DB 롤백, 생성된 파일은 삭제 처리

## 로깅
- `logback-spring.xml` 사용
- 로그에 `traceId` 포함
- 기본 로그 경로: `./logs`
