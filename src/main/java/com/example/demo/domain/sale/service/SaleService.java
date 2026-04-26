package com.example.demo.domain.sale.service;

import com.example.demo.domain.course.entity.Course;
import com.example.demo.domain.course.repository.CourseRepository;
import com.example.demo.domain.sale.dto.SaleRequestDto;
import com.example.demo.domain.sale.dto.SaleResponseDto;
import com.example.demo.domain.sale.entity.Sale;
import com.example.demo.domain.sale.repository.SaleRepository;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleService {

	private final SaleRepository saleRepository;
	private final CourseRepository courseRepository;

	@Transactional
	public void createSale(SaleRequestDto request) {
		//강의 참조 실패
		Course course = courseRepository.findByIdAndIsValidTrue(request.getCourseId())
			.orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

		Sale sale = Sale.builder()
			.course(course)
			.studentId(request.getStudentId())
			.amount(request.getAmount())
			.paidAt(request.getPaidAt())
			.build();

		saleRepository.save(sale);
	}

	@Transactional(readOnly = true)
	public List<SaleResponseDto> getSales(Long creatorId, LocalDate startDate, LocalDate endDate) {
		OffsetDateTime startDateTime = startDate.atStartOfDay().atOffset(ZoneOffset.ofHours(9));
		OffsetDateTime endDateTime = endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.ofHours(9));

		return saleRepository
			.findSalesByCreatorIdAndPaidAtBetween(
				creatorId,
				startDateTime,
				endDateTime
			)
			.stream()
			.map(SaleResponseDto::from)
			.toList();
	}
}
