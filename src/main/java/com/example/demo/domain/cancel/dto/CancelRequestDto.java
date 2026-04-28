package com.example.demo.domain.cancel.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequestDto {

	//취소할 판매
	private Long saleId;

	//취소 금액
	private Integer refundAmount;

	//취소 일자
	private OffsetDateTime canceledAt;
}
