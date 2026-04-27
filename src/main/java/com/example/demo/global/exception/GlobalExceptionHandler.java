package com.example.demo.global.exception;

import com.example.demo.global.response.ApiResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResult<Void>> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ApiResult.failed(errorCode.getStatus().value(), exception));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResult<Void>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException exception
	) {
		Class<?> requiredType = exception.getRequiredType();

		if (requiredType == LocalDate.class
			|| requiredType == LocalDateTime.class
			|| requiredType == OffsetDateTime.class
			|| requiredType == YearMonth.class) {
			BusinessException businessException = new BusinessException(ErrorCode.INVALID_DATE_FORMAT);
			ErrorCode errorCode = businessException.getErrorCode();

			return ResponseEntity
				.status(errorCode.getStatus())
				.body(ApiResult.failed(errorCode.getStatus().value(), businessException));
		}

		return ResponseEntity.badRequest()
			.body(ApiResult.failed(400, exception));
	}
}
