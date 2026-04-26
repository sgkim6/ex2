package com.example.demo.domain.creator.dto;

import com.example.demo.domain.creator.entity.Creator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatorDto {

	private String name;

	public Creator toEntity() {
		return new Creator(name);
	}
}
