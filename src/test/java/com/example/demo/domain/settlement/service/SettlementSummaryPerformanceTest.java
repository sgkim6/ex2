package com.example.demo.domain.settlement.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.domain.cancel.dto.CancelListDto;
import com.example.demo.domain.cancel.entity.Cancel;
import com.example.demo.domain.cancel.repository.CancelRepository;
import com.example.demo.domain.course.dto.CourseListDto;
import com.example.demo.domain.course.entity.Course;
import com.example.demo.domain.course.repository.CourseRepository;
import com.example.demo.domain.creator.dto.CreatorListDto;
import com.example.demo.domain.creator.entity.Creator;
import com.example.demo.domain.creator.repository.CreatorRepository;
import com.example.demo.domain.sale.dto.SaleListDto;
import com.example.demo.domain.sale.entity.Sale;
import com.example.demo.domain.sale.repository.SaleRepository;
import com.example.demo.domain.settlement.dto.SettlementResponseDto;
import com.example.demo.domain.settlement.dto.SettlementSummaryRequestDto;
import com.example.demo.domain.settlement.dto.SettlementSummaryResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Tag("performance")
@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:performance-db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"settlement.fee-rate=0.20"
})
@ActiveProfiles("performance")
class SettlementSummaryPerformanceTest {

	private static final ZoneOffset KST = ZoneOffset.ofHours(9);

	@Autowired
	private SettlementService settlementService;

	@Autowired
	private CreatorRepository creatorRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private CancelRepository cancelRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws Exception {
		if (creatorRepository.count() > 0) {
			return;
		}

		try (InputStream is = getClass().getClassLoader().getResourceAsStream("performance-data.json")) {
			if (is != null) {
				PerformanceSeedData data = objectMapper.readValue(is, PerformanceSeedData.class);
				if (!data.isEmpty()) {
					seedFromJson(data);
					return;
				}
			}
		}

		seedGeneratedData();
	}

	@Test
	@DisplayName("강사의 특정 월 정산 조회 성능 측정")
	void measureSingleSettlementQueryTime() {
		long start = System.nanoTime();
		SettlementResponseDto result = settlementService.getSettlement(1L, YearMonth.of(2025, 3));
		long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

		System.out.println("single settlement elapsed = " + elapsedMillis + " ms");
		System.out.println("single settlement expected amount = " + result.getExpectedSettlementAmount());

		assertNotNull(result);
		assertEquals("2025-03", result.getYearMonth());
	}

	@Test
	@DisplayName("운영자용 정산 집계 조회 성능 측정")
	void measureSettlementSummaryQueryTime() {
		SettlementSummaryRequestDto request = new SettlementSummaryRequestDto(
			YearMonth.of(2025, 1),
			YearMonth.of(2025, 12)
		);

		long start = System.nanoTime();
		SettlementSummaryResponseDto result = settlementService.getSettlementSummaries(request);
		long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

		System.out.println("settlement summary elapsed = " + elapsedMillis + " ms");
		System.out.println("settlement summary count = " + result.getSettlements().size());
		System.out.println("settlement summary total = " + result.getTotalExpectedSettlementAmount());

		assertNotNull(result);
		assertFalse(result.getSettlements().isEmpty());
	}

	private void seedFromJson(PerformanceSeedData data) {
		List<Creator> creators = new ArrayList<>();
		data.creators().getCreators().forEach(dto -> creators.add(creatorRepository.save(dto.toEntity())));

		List<Course> courses = new ArrayList<>();
		data.courses().getCourses().forEach(dto -> {
			Creator creator = creators.get(dto.getCreatorId().intValue() - 1);
			courses.add(courseRepository.save(dto.toEntity(creator)));
		});

		List<Sale> sales = new ArrayList<>();
		data.sales().getSaleRecords().forEach(dto -> {
			Course course = courses.get(dto.getCourseId().intValue() - 1);
			sales.add(saleRepository.save(dto.toEntity(course)));
		});

		data.cancels().getCancels().forEach(dto -> {
			Sale sale = sales.get(dto.getSaleId().intValue() - 1);
			cancelRepository.save(dto.toEntity(sale));
		});
	}

	private void seedGeneratedData() {
		List<Creator> creators = new ArrayList<>();
		for (int i = 1; i <= 50; i++) {
			Creator creator = creatorRepository.save(
				Creator.builder()
					.name("creator-" + i)
					.build()
			);
			creators.add(creator);
		}

		List<Course> courses = new ArrayList<>();
		for (Creator creator : creators) {
			for (int i = 1; i <= 2; i++) {
				Course course = courseRepository.save(
					Course.builder()
						.creator(creator)
						.title("course-" + creator.getId() + "-" + i)
						.build()
				);
				courses.add(course);
			}
		}

		List<Sale> sales = new ArrayList<>();
		int saleCursor = 0;
		for (int month = 1; month <= 12; month++) {
			for (Course course : courses) {
				for (int i = 1; i <= 10; i++) {
					OffsetDateTime paidAt = OffsetDateTime.of(
						2025,
						month,
						(i % 28) + 1,
						10,
						0,
						0,
						0,
						KST
					);

					Sale sale = saleRepository.save(
						Sale.builder()
							.course(course)
							.studentId("student-" + saleCursor)
							.amount(50000 + (saleCursor % 5) * 10000)
							.paidAt(paidAt)
							.build()
					);
					sales.add(sale);

					if (saleCursor % 5 == 0) {
						cancelRepository.save(
							Cancel.builder()
								.sale(sale)
								.refundAmount((saleCursor % 2 == 0) ? sale.getAmount() : 30000)
								.canceledAt(paidAt.plusDays(3))
								.build()
						);
					}

					saleCursor++;
				}
			}
		}
	}

	private record PerformanceSeedData(
		CreatorListDto creators,
		CourseListDto courses,
		SaleListDto sales,
		CancelListDto cancels
	) {
		boolean isEmpty() {
			return creators == null || creators.getCreators() == null || creators.getCreators().isEmpty();
		}
	}
}
