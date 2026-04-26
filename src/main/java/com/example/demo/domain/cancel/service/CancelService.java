package com.example.demo.domain.cancel.service;

import com.example.demo.domain.cancel.dto.CancelRequestDto;
import com.example.demo.domain.cancel.entity.Cancel;
import com.example.demo.domain.cancel.repository.CancelRepository;
import com.example.demo.domain.sale.entity.Sale;
import com.example.demo.domain.sale.repository.SaleRepository;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelService {

	private final CancelRepository cancelRepository;
	private final SaleRepository saleRepository;

	@Transactional
	public void createCancel(CancelRequestDto request) {
		Sale sale = saleRepository.findByIdAndIsValidTrue(request.getSaleId())
			.orElseThrow(() -> new BusinessException(ErrorCode.SALE_NOT_FOUND));

		Integer refundedAmount = cancelRepository.sumRefundAmountBySaleId(sale.getId());
		Integer refundableAmount = sale.getAmount() - refundedAmount;

		if (refundableAmount < request.getRefundAmount()) {
			throw new BusinessException(ErrorCode.REFUND_AMOUNT_EXCEEDED);
		}

		Cancel cancel = Cancel.builder()
			.sale(sale)
			.refundAmount(request.getRefundAmount())
			.canceledAt(request.getCanceledAt())
			.build();

		cancelRepository.save(cancel);
	}
}
