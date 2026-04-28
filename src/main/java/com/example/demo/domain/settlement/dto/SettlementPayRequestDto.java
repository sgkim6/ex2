package com.example.demo.domain.settlement.dto;

import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPayRequestDto {

	private Long creatorId;
	private YearMonth yearMonth;
}
