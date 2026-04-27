package com.example.demo.domain.settlement.repository;

import com.example.demo.domain.settlement.entity.Settlement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

	@Query("""
		select s
		from Settlement s
		join fetch s.creator c
		where c.id = :creatorId
		  and s.settlementMonth = :settlementMonth
		  and s.isValid = true
		""")
	Optional<Settlement> findByCreatorIdAndSettlementMonth(
		@Param("creatorId") Long creatorId,
		@Param("settlementMonth") String settlementMonth
	);
}
