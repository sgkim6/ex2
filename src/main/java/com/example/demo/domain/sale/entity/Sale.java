package com.example.demo.domain.sale.entity;

import com.example.demo.domain.course.entity.Course;
import com.example.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "sales")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(nullable = false)
	private String studentId;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	private OffsetDateTime paidAt;

	@Builder
	private Sale(Course course, String studentId, Integer amount, OffsetDateTime paidAt) {
		this.course = course;
		this.studentId = studentId;
		this.amount = amount;
		this.paidAt = paidAt;
	}
}
