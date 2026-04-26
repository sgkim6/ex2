package com.example.demo.global.exception;

import com.example.demo.global.response.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResult<Void>> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ApiResult.failed(errorCode.getStatus().value(), exception));
	}
}
