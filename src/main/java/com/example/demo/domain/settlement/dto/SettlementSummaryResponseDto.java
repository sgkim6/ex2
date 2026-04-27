package com.example.demo.domain.settlement.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementSummaryResponseDto {

	private String startYearMonth;
	private String endYearMonth;
	private List<SettlementSummaryItemDto> settlements; // 모든 정산 데이터
	private Integer totalExpectedSettlementAmount; // 전체 크리에이터 정산액 합계

	public static SettlementSummaryResponseDto of(
		String startYearMonth,
		String endYearMonth,
		List<SettlementSummaryItemDto> settlements,
		Integer totalExpectedSettlementAmount
	) {
		return SettlementSummaryResponseDto.builder()
			.startYearMonth(startYearMonth)
			.endYearMonth(endYearMonth)
			.settlements(settlements)
			.totalExpectedSettlementAmount(totalExpectedSettlementAmount)
			.build();
	}
}
