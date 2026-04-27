package com.example.demo.domain.sale.dto;

import com.example.demo.domain.sale.entity.Sale;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleResponseDto {

	private final Long saleId;
	private final Long courseId;
	private final String courseTitle;
	private final String studentId;
	private final Integer amount; // 판매액
	private final OffsetDateTime paidAt;

	public static SaleResponseDto from(Sale sale) {
		return SaleResponseDto.builder()
			.saleId(sale.getId())
			.courseId(sale.getCourse().getId())
			.courseTitle(sale.getCourse().getTitle())
			.studentId(sale.getStudentId())
			.amount(sale.getAmount())
			.paidAt(sale.getPaidAt())
			.build();
	}
}
