package com.example.demo.domain.settlement.service;

import com.example.demo.domain.cancel.entity.Cancel;
import com.example.demo.domain.cancel.repository.CancelRepository;
import com.example.demo.domain.creator.entity.Creator;
import com.example.demo.domain.creator.repository.CreatorRepository;
import com.example.demo.domain.sale.entity.Sale;
import com.example.demo.domain.sale.repository.SaleRepository;
import com.example.demo.domain.settlement.dto.SettlementPayRequestDto;
import com.example.demo.domain.settlement.dto.SettlementResponseDto;
import com.example.demo.domain.settlement.dto.SettlementRequestDto;
import com.example.demo.domain.settlement.dto.SettlementSummaryItemDto;
import com.example.demo.domain.settlement.dto.SettlementSummaryResponseDto;
import com.example.demo.domain.settlement.dto.SettlementSummaryRequestDto;
import com.example.demo.domain.settlement.entity.Settlement;
import com.example.demo.domain.settlement.entity.SettlementStatus;
import com.example.demo.domain.settlement.repository.SettlementRepository;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

	private final SettlementRepository settlementRepository;
	private final CreatorRepository creatorRepository;
	private final SaleRepository saleRepository;
	private final CancelRepository cancelRepository;
	@Value("${settlement.fee-rate}")
	private BigDecimal feeRate;

	@Transactional
	public SettlementResponseDto getSettlement(Long creatorId, YearMonth yearMonth) {
		String settlementMonth = yearMonth.toString();

		return settlementRepository.findByCreatorIdAndSettlementMonth(creatorId, settlementMonth)
			.map(this::toResponseDto)
			.orElseGet(() -> calculateSettlement(creatorId, yearMonth));
	}

	@Transactional
	public SettlementResponseDto confirmSettlement(SettlementRequestDto request) {
		YearMonth currentMonth = YearMonth.now();
		String settlementMonth = request.getYearMonth().toString();

		// 정산 날짜 정합성 검사
		if (!request.getYearMonth().isBefore(currentMonth)) {
			throw new BusinessException(ErrorCode.INVALID_SETTLEMENT_MONTH);
		}

		// 중복 정산 검사
		if (settlementRepository.findByCreatorIdAndSettlementMonth(request.getCreatorId(), settlementMonth).isPresent()) {
			throw new BusinessException(ErrorCode.SETTLEMENT_ALREADY_PROCESSED);
		}
		// 정산 예상금액 계산
		SettlementResponseDto settlementResponseDto = calculateSettlement(request.getCreatorId(), request.getYearMonth());
		Creator creator = creatorRepository.findById(request.getCreatorId())
			.orElseThrow(() -> new BusinessException(ErrorCode.CREATOR_NOT_FOUND));

		Settlement settlement = Settlement.builder()
			.creator(creator)
			.settlementMonth(settlementResponseDto.getYearMonth())
			.totalSalesAmount(settlementResponseDto.getTotalSalesAmount())
			.totalRefundAmount(settlementResponseDto.getTotalRefundAmount())
			.feeRate(settlementResponseDto.getFeeRate())
			.salesCount(settlementResponseDto.getSalesCount())
			.cancelCount(settlementResponseDto.getCancelCount())
			.status(SettlementStatus.CONFIRM)
			.build();

		Settlement savedSettlement = settlementRepository.save(settlement);
		return toResponseDto(savedSettlement);
	}

	@Transactional
	public SettlementResponseDto paySettlement(SettlementPayRequestDto request) {
		YearMonth currentMonth = YearMonth.now();
		String settlementMonth = request.getYearMonth().toString();

		if (!request.getYearMonth().isBefore(currentMonth)) {
			throw new BusinessException(ErrorCode.INVALID_SETTLEMENT_MONTH);
		}

		Settlement settlement = settlementRepository.findByCreatorIdAndSettlementMonth(request.getCreatorId(), settlementMonth)
			.orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));

		if (settlement.getStatus() == SettlementStatus.PAID) {
			throw new BusinessException(ErrorCode.SETTLEMENT_ALREADY_PAID);
		}

		settlement.markAsPaid();
		return toResponseDto(settlement);
	}


	// 운영자용 정산 집계
	@Transactional(readOnly = true)
	public SettlementSummaryResponseDto getSettlementSummaries(SettlementSummaryRequestDto request) {
		if (request.getStartYearMonth().isAfter(request.getEndYearMonth())) {
			throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
		}

		List<SettlementSummaryItemDto> settlements = new ArrayList<>(); // 정산 담을 dto
		long totalExpectedSettlementAmount = 0L; // 모든 강사의 정산액 합계
		List<Creator> creators = creatorRepository.findAll(); // 전체 강사

		/*
			우선 요청받은 모든 월을 순회하며, 해당 월의 모든 강사 id에 대한 정산 조회
			if 정산 존재 : 그대로 입력
			elif 정산 미존재 : 정산 산출 메서드 써서 PENDING 넣기, 판/환 0일경우 아예 로그가 없는거니까 걍 무시
		 */
		for (YearMonth yearMonth = request.getStartYearMonth();
			 !yearMonth.isAfter(request.getEndYearMonth());
			 yearMonth = yearMonth.plusMonths(1)) {
			YearMonth targetYearMonth = yearMonth;
			String settlementMonth = yearMonth.toString();

			for (Creator creator : creators) {
				if (!creator.getIsValid()) {
					continue;
				}

				// 정산 있으면 사용, 없으면 집계
				SettlementResponseDto settlementResponseDto = settlementRepository
					.findByCreatorIdAndSettlementMonth(creator.getId(), settlementMonth)
					.map(this::toResponseDto)
					.orElseGet(() -> calculateSettlement(creator.getId(), targetYearMonth));

				// 집계한거 판매 환불 다없으면 무시
				if (settlementResponseDto.getSalesCount() == 0 && settlementResponseDto.getCancelCount() == 0) {
					continue;
				}

				settlements.add(SettlementSummaryItemDto.of(
					creator.getId(),
					creator.getName(),
					settlementResponseDto.getYearMonth(),
					settlementResponseDto.getExpectedSettlementAmount().longValue(),
					settlementResponseDto.getStatus()
				));
				totalExpectedSettlementAmount += settlementResponseDto.getExpectedSettlementAmount().longValue();
			}
		}

		return SettlementSummaryResponseDto.of(
			request.getStartYearMonth().toString(),
			request.getEndYearMonth().toString(),
			settlements,
			totalExpectedSettlementAmount
		);
	}

	// 해당월 정산 없을경우 원천데이터로 계산
	private SettlementResponseDto calculateSettlement(Long creatorId, YearMonth yearMonth) {
		Creator creator = creatorRepository.findById(creatorId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CREATOR_NOT_FOUND));

		ZoneId zoneId = ZoneId.systemDefault();
		OffsetDateTime startDateTime = yearMonth.atDay(1).atStartOfDay(zoneId).toOffsetDateTime();
		OffsetDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(zoneId).toOffsetDateTime();

		// 해당 강사 모든 판매/취소 불러오기
		List<Sale> sales = saleRepository.findSalesByCreatorIdAndMonth(creatorId, startDateTime, endDateTime);
		List<Cancel> cancels = cancelRepository.findCancelsByCreatorIdAndMonth(creatorId, startDateTime, endDateTime);

		// 총액 계산
		int totalSalesAmount = sales.stream()
			.mapToInt(Sale::getAmount)
			.sum();
		int totalRefundAmount = cancels.stream()
			.mapToInt(Cancel::getRefundAmount)
			.sum();
		int netSalesAmount = totalSalesAmount - totalRefundAmount;
		int feeAmount = BigDecimal.valueOf(netSalesAmount)
			.multiply(feeRate)
			.setScale(0, RoundingMode.DOWN)
			.intValue();
		int expectedSettlementAmount = netSalesAmount - feeAmount;

		return SettlementResponseDto.of(
			creator.getId(),
			yearMonth.toString(),
			totalSalesAmount,
			totalRefundAmount,
			netSalesAmount,
			feeRate,
			feeAmount,
			expectedSettlementAmount,
			sales.size(),
			cancels.size(),
			SettlementStatus.PENDING
		);
	}

	private SettlementResponseDto toResponseDto(Settlement settlement) {
		int totalSalesAmount = settlement.getTotalSalesAmount();
		int totalRefundAmount = settlement.getTotalRefundAmount();
		int netSalesAmount = totalSalesAmount - totalRefundAmount;
		int feeAmount = BigDecimal.valueOf(netSalesAmount)
			.multiply(settlement.getFeeRate())
			.setScale(0, RoundingMode.DOWN)
			.intValue();
		int expectedSettlementAmount = netSalesAmount - feeAmount;

		return SettlementResponseDto.of(
			settlement.getCreator().getId(),
			settlement.getSettlementMonth(),
			totalSalesAmount,
			totalRefundAmount,
			netSalesAmount,
			settlement.getFeeRate(),
			feeAmount,
			expectedSettlementAmount,
			settlement.getSalesCount(),
			settlement.getCancelCount(),
			settlement.getStatus()
		);
	}
}
