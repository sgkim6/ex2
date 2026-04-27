package com.example.demo.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	CREATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "E000", "크리에이터를 찾을 수 없습니다."),
	COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "강의를 찾을 수 없습니다."),
	SALE_NOT_FOUND(HttpStatus.NOT_FOUND, "E002", "판매 내역을 찾을 수 없습니다."),
	REFUND_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "E003", "환불 가능 금액을 초과했습니다."),
	INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "E004", "시작일은 종료일보다 늦을 수 없습니다."),
	;

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
