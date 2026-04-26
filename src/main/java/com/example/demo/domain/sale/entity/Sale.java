package com.example.demo.domain.sale.entity;

import com.example.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "sales")
@NoArgsConstructor
public class Sale extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String externalId;

	@Column(nullable = false)
	private String courseId;

	@Column(nullable = false)
	private String studentId;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	private OffsetDateTime paidAt;

	public Sale(String externalId, String courseId, String studentId, Integer amount, OffsetDateTime paidAt) {
		this.externalId = externalId;
		this.courseId = courseId;
		this.studentId = studentId;
		this.amount = amount;
		this.paidAt = paidAt;
	}
}
