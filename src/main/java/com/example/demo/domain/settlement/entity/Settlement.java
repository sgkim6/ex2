package com.example.demo.domain.settlement.entity;

import com.example.demo.domain.creator.entity.Creator;
import com.example.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "settlements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id", nullable = false)
	private Creator creator;

	//총 판매
	@Column(nullable = false)
	private Integer totalSalesAmount;

	// 총 환불
	@Column(nullable = false)
	private Integer totalRefundAmount;

	// 적용 수수료율
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal feeRate;

	// 판매건수
	@Column(nullable = false)
	private Integer salesCount;

	// 취소건수
	@Column(nullable = false)
	private Integer cancelCount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SettlementStatus status;

	@Builder
	private Settlement(
		Creator creator,
		Integer totalSalesAmount,
		Integer totalRefundAmount,
		BigDecimal feeRate,
		Integer salesCount,
		Integer cancelCount,
		SettlementStatus status
	) {
		this.creator = creator;
		this.totalSalesAmount = totalSalesAmount;
		this.totalRefundAmount = totalRefundAmount;
		this.feeRate = feeRate;
		this.salesCount = salesCount;
		this.cancelCount = cancelCount;
		this.status = status;
	}
}
