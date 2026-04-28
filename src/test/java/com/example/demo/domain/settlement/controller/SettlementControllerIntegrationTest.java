package com.example.demo.domain.settlement.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:controller-testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class SettlementControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("잘못된 yearMonth 형식으로 요청하면 INVALID_DATE_FORMAT 응답을 반환한다")
	void getSettlement_invalidYearMonth_returnsBadRequest() throws Exception {
		mockMvc.perform(
				get("/api/v1/settlements")
					.param("creatorId", "1")
					.param("yearMonth", "2025-13")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.msg").value("날짜 형식이 올바르지 않습니다."));
	}
}
