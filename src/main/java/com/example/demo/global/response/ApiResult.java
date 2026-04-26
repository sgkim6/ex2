package com.example.demo.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

	private final int status;
	private final String msg;
	private final T data;

	public static <T> ApiResult<T> succeed(T data) {
		return succeed(data, null);
	}

	public static <T> ApiResult<T> succeed(T data, String msg) {
		return new ApiResult<>(200, msg, data);
	}

	public static ApiResult<Void> failed(int status, Throwable throwable) {
		return new ApiResult<>(status, throwable.getMessage(), null);
	}
}
