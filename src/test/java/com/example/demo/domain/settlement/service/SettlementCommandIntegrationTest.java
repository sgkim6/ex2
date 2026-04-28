package com.example.demo.domain.settlement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.domain.settlement.dto.SettlementPayRequestDto;
import com.example.demo.domain.settlement.dto.SettlementRequestDto;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import java.time.YearMonth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:command-testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("local")
@Transactional
class SettlementCommandIntegrationTest {

	@Autowired
	private SettlementService settlementService;

	@Test
	@DisplayName("동일 기간 정산을 두 번 확정하면 예외를 던진다")
	void confirmSettlement_twice_throwsException() {
		SettlementRequestDto request = new SettlementRequestDto(1L, YearMonth.of(2025, 3));

		settlementService.confirmSettlement(request);

		BusinessException exception = assertThrows(
			BusinessException.class,
			() -> settlementService.confirmSettlement(request)
		);

		assertEquals(ErrorCode.SETTLEMENT_ALREADY_PROCESSED, exception.getErrorCode());
	}

	@Test
	@DisplayName("이미 지급된 정산을 다시 지급하면 예외를 던진다")
	void paySettlement_twice_throwsException() {
		SettlementRequestDto confirmRequest = new SettlementRequestDto(1L, YearMonth.of(2025, 3));
		SettlementPayRequestDto payRequest = new SettlementPayRequestDto(1L, YearMonth.of(2025, 3));

		settlementService.confirmSettlement(confirmRequest);
		settlementService.paySettlement(payRequest);

		BusinessException exception = assertThrows(
			BusinessException.class,
			() -> settlementService.paySettlement(payRequest)
		);

		assertEquals(ErrorCode.SETTLEMENT_ALREADY_PAID, exception.getErrorCode());
	}

	@Test
	@DisplayName("현재 월 정산은 확정할 수 없다")
	void confirmSettlement_currentMonth_throwsException() {
		SettlementRequestDto request = new SettlementRequestDto(1L, YearMonth.now());

		BusinessException exception = assertThrows(
			BusinessException.class,
			() -> settlementService.confirmSettlement(request)
		);

		assertEquals(ErrorCode.INVALID_SETTLEMENT_MONTH, exception.getErrorCode());
	}

	@Test
	@DisplayName("미래 월 정산은 지급할 수 없다")
	void paySettlement_futureMonth_throwsException() {
		SettlementPayRequestDto request = new SettlementPayRequestDto(1L, YearMonth.now().plusMonths(1));

		BusinessException exception = assertThrows(
			BusinessException.class,
			() -> settlementService.paySettlement(request)
		);

		assertEquals(ErrorCode.INVALID_SETTLEMENT_MONTH, exception.getErrorCode());
	}
}
