package com.example.demo.domain.settlement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.domain.settlement.dto.SettlementResponseDto;
import com.example.demo.domain.settlement.entity.SettlementStatus;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("local")
class SettlementServiceIntegrationTest {

	@Autowired
	private SettlementService settlementService;

	@Test
	@DisplayName("creator-1의 2025-03 정산은 총 판매 260000, 환불 110000, 순 판매 150000, 수수료 30000, 정산 예정 120000이다")
	void getSettlement_creator1_march_returnsExpectedAmounts() {
		SettlementResponseDto result = settlementService.getSettlement(1L, YearMonth.of(2025, 3));

		assertEquals(1L, result.getCreatorId());
		assertEquals("2025-03", result.getYearMonth());
		assertEquals(260000, result.getTotalSalesAmount());
		assertEquals(110000, result.getTotalRefundAmount());
		assertEquals(150000, result.getNetSalesAmount());
		assertEquals(0, new BigDecimal("0.20").compareTo(result.getFeeRate()));
		assertEquals(30000, result.getFeeAmount());
		assertEquals(120000, result.getExpectedSettlementAmount());
		assertEquals(4, result.getSalesCount());
		assertEquals(2, result.getCancelCount());
		assertEquals(SettlementStatus.PENDING, result.getStatus());
	}

	@Test
	@DisplayName("부분 환불은 원결제 금액보다 작은 환불액만큼만 순 판매에 반영된다")
	void getSettlement_partialRefund_isReflectedInNetSales() {
		SettlementResponseDto result = settlementService.getSettlement(1L, YearMonth.of(2025, 3));

		assertEquals(110000, result.getTotalRefundAmount());
		assertEquals(150000, result.getNetSalesAmount());
		assertEquals(120000, result.getExpectedSettlementAmount());
	}

	@Test
	@DisplayName("월 경계 취소는 판매월과 취소월에 각각 분리 반영된다")
	void getSettlement_monthBoundaryCancel_isAllocatedToEachMonth() {
		SettlementResponseDto januaryResult = settlementService.getSettlement(2L, YearMonth.of(2025, 1));
		SettlementResponseDto februaryResult = settlementService.getSettlement(2L, YearMonth.of(2025, 2));

		assertEquals(60000, januaryResult.getTotalSalesAmount());
		assertEquals(0, januaryResult.getTotalRefundAmount());
		assertEquals(1, januaryResult.getSalesCount());
		assertEquals(0, januaryResult.getCancelCount());

		assertEquals(0, februaryResult.getTotalSalesAmount());
		assertEquals(60000, februaryResult.getTotalRefundAmount());
		assertEquals(0, februaryResult.getSalesCount());
		assertEquals(1, februaryResult.getCancelCount());
	}

	@Test
	@DisplayName("판매 내역이 없는 월은 단일 조회 시 0원 응답과 PENDING 상태로 반환된다")
	void getSettlement_emptyMonth_returnsZeroAmounts() {
		SettlementResponseDto result = settlementService.getSettlement(3L, YearMonth.of(2025, 3));

		assertEquals(0, result.getTotalSalesAmount());
		assertEquals(0, result.getTotalRefundAmount());
		assertEquals(0, result.getNetSalesAmount());
		assertEquals(0, result.getFeeAmount());
		assertEquals(0, result.getExpectedSettlementAmount());
		assertEquals(0, result.getSalesCount());
		assertEquals(0, result.getCancelCount());
		assertEquals(SettlementStatus.PENDING, result.getStatus());
	}
}
