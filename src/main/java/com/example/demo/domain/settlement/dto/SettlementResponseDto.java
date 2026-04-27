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
	private Integer totalSalesAmount; // 총 판매금액
	private Integer totalRefundAmount; // 총 환불금액
	private Integer netSalesAmount; // 순 판매금액
	private BigDecimal feeRate; // 당시 적용 수수료율
	private Integer feeAmount; // 수수료
	private Integer expectedSettlementAmount; // 총 정산액
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
