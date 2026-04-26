package com.example.demo.domain.sale.dto;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaleRequestDto {

	private Long courseId;
	private String studentId;
	private Integer amount;
	private OffsetDateTime paidAt;
}
