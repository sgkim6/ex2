package com.example.demo.domain.cancel.repository;

import com.example.demo.domain.cancel.entity.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelRepository extends JpaRepository<Cancel, Long> {
}
