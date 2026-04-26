package com.example.demo.domain.cancel.repository;

import com.example.demo.domain.cancel.entity.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CancelRepository extends JpaRepository<Cancel, Long> {

	@Query("select coalesce(sum(c.refundAmount), 0) from Cancel c where c.sale.id = :saleId and c.isValid = true")
	Integer sumRefundAmountBySaleId(@Param("saleId") Long saleId);
}
