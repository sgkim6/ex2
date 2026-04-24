package com.example.demo.domain.creator.repository;

import com.example.demo.domain.creator.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorRepository extends JpaRepository<Creator, Long> {

	boolean existsByExternalId(String externalId);
}
