package com.example.demo.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "courses")
@NoArgsConstructor
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String externalId;

	@Column(nullable = false)
	private String creatorId;

	@Column(nullable = false)
	private String title;

	public Course(String externalId, String creatorId, String title) {
		this.externalId = externalId;
		this.creatorId = creatorId;
		this.title = title;
	}
}
