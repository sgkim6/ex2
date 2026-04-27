package com.example.demo.domain.sale.repository;

import com.example.demo.domain.sale.entity.Sale;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository extends JpaRepository<Sale, Long> {

	Optional<Sale> findByIdAndIsValidTrue(Long id);

	@Query("""
		select s
		from Sale s
		join fetch s.course c
		where c.creator.id = :creatorId
		  and s.isValid = true
		  and c.isValid = true
		  and s.paidAt between :startDateTime and :endDateTime
		order by s.paidAt desc
		""")
	List<Sale> findSalesByCreatorIdAndPaidAtBetween(
		@Param("creatorId") Long creatorId,
		@Param("startDateTime") OffsetDateTime startDateTime,
		@Param("endDateTime") OffsetDateTime endDateTime
	);

	@Query("""
		select s
		from Sale s
		join fetch s.course c
		where c.creator.id = :creatorId
		  and s.isValid = true
		  and c.isValid = true
		  and s.paidAt >= :startDateTime
		order by s.paidAt desc
		""")
	List<Sale> findSalesByCreatorIdFromStartDateTime(
		@Param("creatorId") Long creatorId,
		@Param("startDateTime") OffsetDateTime startDateTime
	);

	@Query("""
		select s
		from Sale s
		join fetch s.course c
		where c.creator.id = :creatorId
		  and s.isValid = true
		  and c.isValid = true
		  and s.paidAt <= :endDateTime
		order by s.paidAt desc
		""")
	List<Sale> findSalesByCreatorIdUntilEndDateTime(
		@Param("creatorId") Long creatorId,
		@Param("endDateTime") OffsetDateTime endDateTime
	);

	@Query("""
		select s
		from Sale s
		join fetch s.course c
		where c.creator.id = :creatorId
		  and s.isValid = true
		  and c.isValid = true
		order by s.paidAt desc
		""")
	List<Sale> findAllSalesByCreatorId(@Param("creatorId") Long creatorId);

	@Query("""
		select s
		from Sale s
		join fetch s.course c
		where c.creator.id = :creatorId
		  and s.isValid = true
		  and c.isValid = true
		  and s.paidAt between :startDateTime and :endDateTime
		order by s.paidAt desc
		""")
	List<Sale> findSalesByCreatorIdAndMonth(
		@Param("creatorId") Long creatorId,
		@Param("startDateTime") OffsetDateTime startDateTime,
		@Param("endDateTime") OffsetDateTime endDateTime
	);
}
