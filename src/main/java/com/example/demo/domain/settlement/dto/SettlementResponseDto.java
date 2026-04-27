package com.example.demo.domain.settlement.dto;

import com.example.demo.domain.settlement.entity.Settlement;
import com.example.demo.domain.settlement.entity.SettlementStatus;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementResponseDto {

	private Long creatorId;
	private String yearMonth;
	private Integer totalSalesAmount;
	private Integer totalRefundAmount;
	private Integer netSalesAmount;
	private BigDecimal feeRate;
	private Integer feeAmount;
	private Integer expectedSettlementAmount;
	private Integer salesCount;
	private Integer cancelCount;
	private SettlementStatus status;

	public static SettlementResponseDto of(
		Long creatorId,
		String yearMonth,
		Integer totalSalesAmount,
		Integer totalRefundAmount,
		Integer netSalesAmount,
		BigDecimal feeRate,
		Integer feeAmount,
		Integer expectedSettlementAmount,
		Integer salesCount,
		Integer cancelCount,
		SettlementStatus status
	) {
		return SettlementResponseDto.builder()
			.creatorId(creatorId)
			.yearMonth(yearMonth)
			.totalSalesAmount(totalSalesAmount)
			.totalRefundAmount(totalRefundAmount)
			.netSalesAmount(netSalesAmount)
			.feeRate(feeRate)
			.feeAmount(feeAmount)
			.expectedSettlementAmount(expectedSettlementAmount)
			.salesCount(salesCount)
			.cancelCount(cancelCount)
			.status(status)
			.build();
	}
}
