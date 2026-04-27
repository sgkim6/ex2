package com.example.demo.domain.cancel.repository;

import com.example.demo.domain.cancel.entity.Cancel;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CancelRepository extends JpaRepository<Cancel, Long> {

	@Query("select coalesce(sum(c.refundAmount), 0) from Cancel c where c.sale.id = :saleId and c.isValid = true")
	Integer sumRefundAmountBySaleId(@Param("saleId") Long saleId);

	@Query("""
		select c
		from Cancel c
		join fetch c.sale s
		join fetch s.course course
		where course.creator.id = :creatorId
		  and c.isValid = true
		  and s.isValid = true
		  and course.isValid = true
		  and c.canceledAt between :startDateTime and :endDateTime
		order by c.canceledAt desc
		""")
	List<Cancel> findCancelsByCreatorIdAndMonth(
		@Param("creatorId") Long creatorId,
		@Param("startDateTime") OffsetDateTime startDateTime,
		@Param("endDateTime") OffsetDateTime endDateTime
	);
}
