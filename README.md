# 라이브클래스 과제

## 일정

- 4.24
    - 플젝 스캐폴딩
        - 의존성 추가
        - 패키지 구조 생성
        - 엔티티, 이니셜라이저
        - 도메인구조 설계
- 4.25
    - 수강/취소 → 정산 반영 시스템
        - 일부취소
- 4.26
    - 정산 집계(운영자용, 강사용)
- 4.27
    - 테스트 및 성능이슈 해결 고민

## 프로젝트 개요

이 프로젝트는 가상의 온라인 강의 플랫폼에서 발생하는 판매 및 환불 데이터를 기반으로 강사 별 정산 금액을 계산 및 관리하는 시스템입니다.

우선 초기 입력 데이터에 추가로, 강의 판매/환불 데이터를 직접 입력하여 데이터를 구성할 수 있습니다.

강사는 자신이 판매 및 환불한 강의에 대한 월별 정산 내역을 조회할 수 있습니다.

플랫폼 운영자는 특정 기간 동안 전체 크리에이터에 대한 정산 집계를 확인할 수 있고, 특정 크리에이터의 특정 월의 정산을 확정(CONFIRM)하거나, 지급(PAID) 처리할 수 있습니다.

## 기술 스택

Back-End : Java17, Spring Boot 3.3.5, Spring Data JPA

Database : PostgreSQL, H2(test)

Test : Postman, JUnit5

## 실행 방법

1. Docker Compose 실행 (권장) (도커 및 도커 데몬 설치 필요)
    
    ```jsx
    docker compose up --build -d
    
    // 종료
    docker compose down -v
    ```
    
2. 로컬 실행(Docker 실행이 어려울 경우)
    - 사전 조건
        - Java 17
        - PostgreSQL 실행
        - DB name → liveklass 생성
    
    ```json
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/liveklass \
    SPRING_DATASOURCE_USERNAME=<postgres_username> \
    SPRING_DATASOURCE_PASSWORD=<postgres_password> \
    ./gradlew bootRun
    ```
    

## 요구사항 해석 및 가정

1. 정산 및 환불 시점
    - 판매 금액은 결제 완료 시점(paidAt)으로 집계.
    - 환불 금액은 취소 시점(canceledAt)으로 집계.
    - 모든 시간대는 대한민국 표준시 기준으로 통일.
    - 월 경계는 해당 월 1일 00:00:00부터 다음 월 1일 직전까지로 정의.
    - 모든 날짜 입력에 대해 입력 포맷 및 입력 유효성(시작-끝 범위 교차) 검증 및 예외처리 로직 구현.
2. 환불 정책
    - 하나의 판매 건에 대해 여러 건의 부분 환불이 가능하다고 가정.
    - 환불 시 해당 판매 건의 누적 환불 금액이 원 판매 금액을 초과하지 않도록 별도의 검증 로직 추가.
    - 만약 특정 월에 대해 판매액보다 환불액이 더 클 경우가 생길 수 있는데, 일단은 음수의 금액을 가지도록 구현했음.
3. 정산
    - 정산은 다음의 세 가지 상태로 구분하였다
        - PENDING : 원천 데이터(sale, cancel)를 기반으로 계산된 정산 계획 (db 미반영)
        - CONFIRM : 관리자 api를 통해 지난 월에 대해 금액 집계 후 정산 확정 가능 (db 반영)
        - PAID : 이미 확정된 정산을 관리자 api를 통해 PAID로 상태 전이 가능 (db 반영)

## 설계 결정과 이유

- 식별자
    
    초기 데이터에서 제공되는 creator-1과 같은 문자열 식별자를 임의로 제거하고, 별도의 Long 타입 pk를 사용하도록 설계했습니다.
    
- 정산
    
    정산은 지난 월의 거래 내역을 확정하는 개념으로 판단했습니다.
    
    따라서 PENDING 상태의 경우 별도의 정산 엔티티로 저장하지 않고, 강사나 관리자가 아직 정산되지 않은(개념상 PENDING 상태의) 정산을 조회할 경우, 해당 조회 월의 원천 데이터(sale/cancel)를 바탕으로 동적으로 계산하여 던져주게 설계했습니다.
    
    이후 관리자는 지난 월에 대해 정산 확정, 정산 지급 작업을 수행할 수 있습니다. 
    
    정산 확정 시, 해당 월의 판매 및 환불 raw 데이터를 가지고 로직을 태워서 최종 결정된 settlement 엔티티를 db에 넣도록 설계했습니다.
    
    정산 지급은 당장 결제 시스템을 구현하기 어려우므로, 우선은 단순히 관리자가 CONFIRM된 정산에 대해 PAID로 상태를 변경시키는 기능을 별도의 API로 구현했습니다.
    
- 수수료율
    
    수수료율 계산 시 소수점 오차 발생 가능성을 고려하여 double타입 대신 BigDecimal을 사용.
    
    수수료율은 리터럴로 박지 않고 설정 파라미터로 분리하여 코드 수정 없이 변경할 수 있도록 구현.
    

## 미구현 / 제약사항

1. 정산 내역 CSV 파일 다운로드 기능 미구현
2. 운영자 집계 API 성능 이슈
    - 현재는 `월 범위*전체 크리에이터 수` 만큼 순회하는 구조
    - 실제 서비스 환경에서 데이터 규모가 커지면 잠재적으로 문제가 생길 것 같음.
    - 월 판매 데이터 10,000건, 환불 데이터 5,000건으로 테스트 진행 결과
        - 개인 강사의 정산 예정 조회 시 약 6.2s 소요
        - 최적화 필요할거같음

## AI 활용 범위

<aside>
💡

본 프로젝트에서는 개발 생산성을 향상시키기 위해 AI를 보조 도구로 활용하였습니다.
다만, 핵심 비즈니스 로직과 설계는 직접 구현하는 것을 원칙으로 진행하였습니다.

</aside>

- 사용 AI 도구
    - OpenAI Codex gpt-5.4
1. 초기 프로젝트 스카폴딩
    - 기본 응답 포맷
    - 공통 에러 처리 구조
    - 패키지 구조 등
    
    이전에 진행했던 스프링 부트 프로젝트 컨벤션에 맞게 자동으로 개발 환경을 구축하는 데 사용.
    
2. API 스펙(요청/응답 DTO) 생성
    
    요구사항을 자연어로 정리한 후, 이를 기반으로 다음 작업에 AI를 활용
    
    - Request/Response Dto 생성
    - 필드 네이밍
    
    실제 필드 구성의 적절성 및 도메인 의미는 직접 검토 후 수정했습니다.
    
3. 코드 리팩토링 보조
    
    직접 구현한 서비스 및 레포지토리 메서드의 코드 구조 및 가독성 향상.
    
    - 중복 코드 정리
    - 코드 리뷰 및 구조 개선 제안 검토
4. 테스트 코드 작성 및 mock 데이터 생성
    - 자연어로 현재 진행 할 테스트에 대한 가상의 mock 데이터 생성
5. 활용 범위 제한
    
    다음과 같은 핵심 영역은 AI에 의존하지 않고 직접 판단 및 적용
    
    - 도메인 설계 (정산 및 환불 정책)
    - 핵심 비즈니스 로직 및 쿼리
    - 데이터 모델링, 엔티티 설계

## API 목록 및 예시

### 1. Sale

1. **판매 등록**
    
    엔드포인트:`POST /api/v1/sales`
    
    요청 스펙:
    
    - `courseId: Long`
    - `studentId: String`
    - `amount: Integer`
    - `paidAt: OffsetDateTime`
    
    요청 예시:
    
    ```jsx
    {
    	"courseId": 1,
    	"studentId": "student-100",
    	"amount": 50000,
    	"paidAt": "2025-03-30T10:00:00+09:00"
    }
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": "판매 등록 완료",
    	"data": null
    }
    ```
    
2. **강사 판매 내역 조회**
    
    엔드포인트:`GET /api/v1/sales`
    
    요청 스펙:
    
    - creatorId: Long
    - startDate: yyyy-MM-dd optional
    - endDate: yyyy-MM-dd optional
    
    요청 예시:
    
    ```jsx
    {
    	"creatorId": 1,
    	"startDate": "2025-03-01",
    	"endDate": "2025-03-31"
    }
    ```
    
    실제 호출 예시:
    
    ```jsx
    GET /api/v1/sales?creatorId=1&startDate=2025-03-01&endDate=2025-03-31
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": null,
    	"data": [
    		{
    		"saleId": 4,
    		"courseId": 2,
    		"courseTitle": "JPA 실전",
    		"studentId": "student-4",
    		"amount": 80000,
    		"paidAt": "2025-03-22T11:00:00+09:00"
    		}
    	]
    }
    ```
    

### 2. Cancel

1. **취소 등록**
엔드포인트:`POST /api/v1/cancels`
    
    요청 스펙:
    
    - `saleId: Long`
    - `refundAmount: Integer`
    - `canceledAt: OffsetDateTime`
    
    요청 예시:
    
    ```jsx
    {
    	"saleId": 4,
    	"refundAmount": 30000,
    	"canceledAt": "2025-03-27T15:00:00+09:00"
    }
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": "취소 등록 완료",
    	"data": null
    }
    ```
    

### 3. Settlement

1. 단일 정산 조회
엔드포인트: `GET /api/v1/settlements`
    
    요청 스펙:
    
    - `creatorId: Long`
    - `yearMonth: yyyy-MM`
    
    요청 예시:
    
    ```jsx
    {
    	"creatorId": 1,
    	"yearMonth": "2025-03"
    }
    ```
    
    실제 호출 예시:
    
    ```jsx
    GET /api/v1/settlements?creatorId=1&yearMonth=2025-03
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": null,
    	"data": {
    		"creatorId": 1,
    		"yearMonth": "2025-03",
    		"totalSalesAmount": 260000,
    		"totalRefundAmount": 110000,
    		"netSalesAmount": 150000,
    		"feeRate": 0.20,
    		"feeAmount": 30000,
    		"expectedSettlementAmount": 120000,
    		"salesCount": 4,
    		"cancelCount": 2,
    		"status": "PENDING"
    	}
    }
    ```
    
2. 정산 확정
엔드포인트: `POST /api/v1/settlements/confirm`
    
    요청 스펙:
    
    - `creatorId: Long`
    - `yearMonth: yyyy-MM`
    
    요청 예시:
    
    ```jsx
    {
    	"creatorId": 1,
    	"yearMonth": "2025-03"
    }
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": "정산 확정 완료",
    	"data": {
    		"creatorId": 1,
    		"yearMonth": "2025-03",
    		"totalSalesAmount": 260000,
    		"totalRefundAmount": 110000,
    		"netSalesAmount": 150000,
    		"feeRate": 0.20,
    		"feeAmount": 30000,
    		"expectedSettlementAmount": 120000,
    		"salesCount": 4,
    		"cancelCount": 2,
    		"status": "CONFIRM"
    	}
    }
    ```
    
3. 정산 지급 처리
엔드포인트: `POST /api/v1/settlements/pay`
    
    요청 스펙:
    
    - `creatorId: Long`
    - `yearMonth: yyyy-MM`
    
    요청 예시:
    
    ```jsx
    {
    	"creatorId": 1,
    	"yearMonth": "2025-03"
    }
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": "정산 지급 완료",
    	"data": {
    		"creatorId": 1,
    		"yearMonth": "2025-03",
    		"totalSalesAmount": 260000,
    		"totalRefundAmount": 110000,
    		"netSalesAmount": 150000,
    		"feeRate": 0.20,
    		"feeAmount": 30000,
    		"expectedSettlementAmount": 120000,
    		"salesCount": 4,
    		"cancelCount": 2,
    		"status": "PAID"
    	}
    }
    ```
    
4. 운영자용 정산 집계 조회
엔드포인트: `GET /api/v1/settlements/summary`
    
    요청 스펙:
    
    - `startYearMonth: yyyy-MM`
    - `endYearMonth: yyyy-MM`
    
    요청 예시:
    
    ```jsx
    {
    	"startYearMonth": "2025-02",
    	"endYearMonth": "2025-03"
    }
    ```
    
    실제 호출 예시:
    
    ```jsx
    GET /api/v1/settlements/summary?startYearMonth=2025-02&endYearMonth=2025-03
    ```
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": "운영자 정산 집계 조회 완료",
    	"data": {
    		"startYearMonth": "2025-02",
    		"endYearMonth": "2025-03",
    		"settlements": [
    			{
    				"creatorId": 1,
    				"creatorName": "김강사",
    				"yearMonth": "2025-03",
    				"expectedSettlementAmount": 120000,
    				"status": "PENDING"
    			},
    			{
    				"creatorId": 2,
    				"creatorName": "이강사",
    				"yearMonth": "2025-02",
    				"expectedSettlementAmount": 48000,
    				"status": "PENDING"
    			}
    	],
    	"totalExpectedSettlementAmount": 168000
    	}
    }
    ```
    

### 공통 응답 형식

1. 성공 응답
    
    응답 예시:
    
    ```jsx
    {
    	"status": 200,
    	"msg": "메시지",
    	"data": {}
    }
    ```
    
2. 실패 응답
    
    응답 예시:
    
    ```jsx
    {
    	"status": 400,
    	"msg": "에러 메시지",
    	"data": null
    }
    ```
    

## 데이터 모델 설명

### 1. 엔티티

1. BaseEntity
    
    공통 메타데이터를 관리하는 상위 엔티티입니다.
    
    주요 필드:
    
    - createdAt: 생성 시각
    - updatedAt: 수정 시각
    - isValid: 소프트딜리트 여부 판단용 플래그
    - deletedAt: 삭제 시각
2. Creator
    
    크리에이터(강사) 정보를 저장하는 엔티티입니다.
    
    주요 필드:
    
    - id: PK
    - name: 강사 이름
    
    설명:
    
    - 정산의 주체입니다.
    - 하나의 크리에이터는 여러 강의를 가질 수 있습니다.
3. Course
    
    강의 정보를 저장하는 엔티티입니다.
    
    주요 필드:
    
    - id: PK
    - creator: 강사 참조
    - title: 강의명
    
    설명:
    
    - 각 강의는 한 명의 크리에이터에 속합니다.
    - 판매 내역은 강의를 기준으로 발생합니다.
4. Sale
    
    판매 내역을 저장하는 엔티티입니다.
    
    주요 필드:
    
    - id: PK
    - course: 강의 참조
    - studentId: 구매 학생 식별자
    - amount: 결제 금액
    - paidAt: 결제 완료 일시
    
    설명:
    
    - 하나의 판매는 하나의 강의에 속합니다.
    - 정산 시 판매 금액 합계의 원천 데이터가 됩니다.
    - 판매 귀속 월은 paidAt 기준으로 판단합니다.
5. Cancel
    
    취소/환불 내역을 저장하는 엔티티입니다.
    
    주요 필드:
    
    - id: PK
    - sale: 원본 판매 참조
    - refundAmount: 환불 금액
    - canceledAt: 취소 일시
    
    설명:
    
    - 하나의 취소는 하나의 판매에 연결됩니다.
    - 부분 환불을 허용합니다.
    - 정산 시 환불 금액 합계의 원천 데이터가 됩니다.
    - 환불 귀속 월은 canceledAt 기준으로 판단합니다.
6. Settlement
    
    정산 확정/지급 시 정산 결과를 저장하는 엔티티입니다.
    
    주요 필드:
    
    - id: PK
    - creator: 정산 대상 강사
    - settlementMonth: 정산 대상 월 (yyyy-MM)
    - totalSalesAmount: 총 판매 금액
    - totalRefundAmount: 총 환불 금액
    - feeRate: 적용 수수료율
    - salesCount: 판매 건수
    - cancelCount: 취소 건수
    - status: 정산 상태 (PENDING, CONFIRM, PAID)
        - 실제 저장값은 CONFIRM, PAID 두 종류만 가짐
    
    설명:
    
    - CONFIRM, PAID 상태의 정산 스냅샷을 저장합니다.
    - PENDING 상태의 정산은 별도로 저장하지 않고 조회 시 계산합니다.
    - 순판매금액, 수수료, 정산예정금액은 저장값으로 중복 관리하지 않고 계산해서 응답합니
    다.

### 2. 관계 요약

- Creator 1 : N Course
- Course 1 : N Sale
- Sale 1 : N Cancel
- Creator 1 : N Settlement

## 테스트 실행 방법

### 전체 테스트 실행

```json
./gradlew test

// 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 테스트 구성

JUnit 기반 통합 테스트

H2 메모리 DB를 사용하며 초기 시드 데이터 기반으로 동작합니다.

추가 테스트의 경우 코드 내에서 mock 데이터 만들어서 테스트했습니다.

별도의 퍼포먼스 테스트는 진행하지 않았으며, 기능 테스트만 진행했습니다.

### 추가 테스트

<aside>
💡

기본 테스트 외에 추가로 진행한 테스트

</aside>

- 동일 기간 정산 중복 확정 시 예외 검증
- 이미 지급된 정산 중복 지급 시 예외 검증
- 현재 월 정산 확정 차단 검증
- 미래 월 정산 지급 차단 검증
- 누적 환불 금액이 원 결제 금액을 초과할 경우 예외 검증
- 잘못된 yearMonth 요청 시 날짜 형식 예외 응답 검증
- 월 시작/월 종료 시각 경계값 데이터가 해당 월 정산에 정확히 포함되는지 검증
