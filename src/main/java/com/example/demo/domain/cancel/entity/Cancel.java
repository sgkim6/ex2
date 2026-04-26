package com.example.demo.domain.cancel.entity;

import com.example.demo.domain.sale.entity.Sale;
import com.example.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cancels")
@NoArgsConstructor
public class Cancel extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sale_id", nullable = false)
	private Sale sale;

	@Column(nullable = false)
	private Integer refundAmount;

	@Column(nullable = false)
	private OffsetDateTime canceledAt;

	public Cancel(Sale sale, Integer refundAmount, OffsetDateTime canceledAt) {
		this.sale = sale;
		this.refundAmount = refundAmount;
		this.canceledAt = canceledAt;
	}
}
