package com.example.demo.domain.sale.dto;

import com.example.demo.domain.course.entity.Course;
import com.example.demo.domain.sale.entity.Sale;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleDto {

	private Long courseId;
	private String studentId;
	private Integer amount;
	private OffsetDateTime paidAt;

	public Sale toEntity(Course course) {
		return Sale.builder()
			.course(course)
			.studentId(studentId)
			.amount(amount)
			.paidAt(paidAt)
			.build();
	}
}
