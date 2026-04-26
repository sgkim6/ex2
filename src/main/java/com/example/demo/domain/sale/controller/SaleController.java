package com.example.demo.domain.sale.controller;

import com.example.demo.domain.sale.dto.SaleRequestDto;
import com.example.demo.domain.sale.dto.SaleResponseDto;
import com.example.demo.domain.sale.service.SaleService;
import com.example.demo.global.response.ApiResult;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.RequiredTypes;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ApiResult<Void> createSale(@RequestBody SaleRequestDto request) {
        saleService.createSale(request);
        return ApiResult.succeed(null, "판매 등록 완료");
    }

    @GetMapping
    public ApiResult<List<SaleResponseDto>> getSales(
        @RequestParam Long creatorId,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate
    ) {
        return ApiResult.succeed(saleService.getSales(creatorId, startDate, endDate));
    }
}
