package com.example.demo.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "강의를 찾을 수 없습니다."),
	SALE_NOT_FOUND(HttpStatus.NOT_FOUND, "E002", "판매 내역을 찾을 수 없습니다."),
	REFUND_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "E003", "환불 가능 금액을 초과했습니다."),
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
