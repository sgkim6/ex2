package com.example.demo.domain.settlement.dto;

import com.example.demo.domain.settlement.entity.SettlementStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementSummaryItemDto {

	private Long creatorId;
	private String creatorName;
	private String yearMonth;
	private Integer expectedSettlementAmount; // 예상 정산금액
	private SettlementStatus status;

	public static SettlementSummaryItemDto of(
		Long creatorId,
		String creatorName,
		String yearMonth,
		Integer expectedSettlementAmount,
		SettlementStatus status
	) {
		return SettlementSummaryItemDto.builder()
			.creatorId(creatorId)
			.creatorName(creatorName)
			.yearMonth(yearMonth)
			.expectedSettlementAmount(expectedSettlementAmount)
			.status(status)
			.build();
	}
}
