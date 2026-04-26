package com.example.demo.domain.sale.controller;

import com.example.demo.domain.sale.dto.SaleRequestDto;
import com.example.demo.domain.sale.service.SaleService;
import com.example.demo.global.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}
