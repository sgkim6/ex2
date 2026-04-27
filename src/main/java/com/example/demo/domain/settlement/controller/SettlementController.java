package com.example.demo.domain.settlement.controller;

import com.example.demo.domain.settlement.dto.SettlementPayRequestDto;
import com.example.demo.domain.settlement.dto.SettlementRequestDto;
import com.example.demo.domain.settlement.dto.SettlementResponseDto;
import com.example.demo.domain.settlement.service.SettlementService;
import com.example.demo.global.response.ApiResult;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {

	private final SettlementService settlementService;

	@GetMapping
	public ApiResult<SettlementResponseDto> getSettlement(
		@RequestParam Long creatorId,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
	) {
		return ApiResult.succeed(settlementService.getSettlement(creatorId, yearMonth));
	}

	@PostMapping("/confirm")
	public ApiResult<SettlementResponseDto> confirmSettlement(@RequestBody SettlementRequestDto request) {
		return ApiResult.succeed(settlementService.confirmSettlement(request), "정산 확정 완료");
	}

	@PostMapping("/pay")
	public ApiResult<SettlementResponseDto> paySettlement(@RequestBody SettlementPayRequestDto request) {
		return ApiResult.succeed(settlementService.paySettlement(request), "정산 지급 완료");
	}
}
