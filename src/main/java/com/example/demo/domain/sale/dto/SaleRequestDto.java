package com.example.demo.domain.sale.dto;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaleRequestDto {

	private Long courseId;
	private String studentId; //학생 id는 일단 문자열로 받고, 나중에 학생 엔티티 추가 시 long으로
	private Integer amount; //금액
	private OffsetDateTime paidAt;
}
