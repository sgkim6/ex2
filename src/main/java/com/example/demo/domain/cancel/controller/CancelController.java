package com.example.demo.domain.cancel.controller;

import com.example.demo.domain.cancel.dto.CancelRequestDto;
import com.example.demo.domain.cancel.service.CancelService;
import com.example.demo.global.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cancels")
public class CancelController {

	private final CancelService cancelService;

	@PostMapping
	public ApiResult<Void> createCancel(@RequestBody CancelRequestDto request) {
		cancelService.createCancel(request);
		return ApiResult.succeed(null, "환불 등록 완료");
	}
}
