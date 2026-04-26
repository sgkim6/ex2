package com.example.demo.domain.creator.entity;

import com.example.demo.global.entity.BaseEntity;
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
@Table(name = "creators")
@NoArgsConstructor
public class Creator extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String externalId; // 외부 아이디(creator-1 ..)

	@Column(nullable = false)
	private String name;

	public Creator(String externalId, String name) {
		this.externalId = externalId;
		this.name = name;
	}
}
