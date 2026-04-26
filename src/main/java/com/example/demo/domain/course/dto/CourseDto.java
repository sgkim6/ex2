package com.example.demo.domain.course.dto;

import com.example.demo.domain.course.entity.Course;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDto {

	private Long creatorId;
	private String title;

	public Course toEntity() {
		return new Course(creatorId, title);
	}
}
