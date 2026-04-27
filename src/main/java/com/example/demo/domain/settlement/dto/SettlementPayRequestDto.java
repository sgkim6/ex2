package com.example.demo.domain.settlement.dto;

import java.time.YearMonth;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettlementPayRequestDto {

	private Long creatorId;
	private YearMonth yearMonth;
}
