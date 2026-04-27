package com.example.demo.domain.cancel.dto;

import com.example.demo.domain.cancel.entity.Cancel;
import com.example.demo.domain.sale.entity.Sale;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelDto {

	private Long saleId;
	private Integer refundAmount;
	private OffsetDateTime canceledAt;

	public Cancel toEntity(Sale sale) {
		return Cancel.builder()
			.sale(sale)
			.refundAmount(refundAmount)
			.canceledAt(canceledAt)
			.build();
	}
}
