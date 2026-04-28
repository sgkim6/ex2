package com.example.demo.domain.cancel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.domain.cancel.dto.CancelRequestDto;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:cancel-testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("local")
class CancelServiceIntegrationTest {

	@Autowired
	private CancelService cancelService;

	@Test
	@DisplayName("누적 환불 금액이 원 결제 금액을 초과하면 예외를 던진다")
	void createCancel_refundAmountExceeded_throwsException() {
		CancelRequestDto request = new CancelRequestDto(
			4L,
			60000,
			OffsetDateTime.of(2025, 3, 28, 10, 0, 0, 0, ZoneOffset.ofHours(9))
		);

		BusinessException exception = assertThrows(
			BusinessException.class,
			() -> cancelService.createCancel(request)
		);

		assertEquals(ErrorCode.REFUND_AMOUNT_EXCEEDED, exception.getErrorCode());
	}
}
