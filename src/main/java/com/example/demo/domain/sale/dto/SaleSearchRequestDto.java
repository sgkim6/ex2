package com.example.demo.domain.sale.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaleSearchRequestDto {

	private Long creatorId;
	private LocalDate startDate;
	private LocalDate endDate;
}
