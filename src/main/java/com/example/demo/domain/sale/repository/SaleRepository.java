package com.example.demo.domain.sale.repository;

import com.example.demo.domain.sale.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
