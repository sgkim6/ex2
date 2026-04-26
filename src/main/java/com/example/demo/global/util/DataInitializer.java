package com.example.demo.global.util;

import com.example.demo.domain.course.dto.CourseDto;
import com.example.demo.domain.course.dto.CourseListDto;
import com.example.demo.domain.course.repository.CourseRepository;
import com.example.demo.domain.creator.entity.Creator;
import com.example.demo.domain.creator.dto.CreatorDto;
import com.example.demo.domain.creator.dto.CreatorListDto;
import com.example.demo.domain.creator.repository.CreatorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class DataInitializer implements CommandLineRunner {

	private final CreatorRepository creatorRepository;
	private final CourseRepository courseRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		if (creatorRepository.count() == 0) {
			CreatorListDto creatorData = readCreatorData();
			for (CreatorDto dto : creatorData.getCreators()) {
				creatorRepository.save(dto.toEntity());
			}
		}

		if (courseRepository.count() == 0) {
			CourseListDto courseData = readCourseData();
			for (CourseDto dto : courseData.getCourses()) {
				Creator creator = creatorRepository.findById(dto.getCreatorId())
					.orElseThrow(() -> new IllegalArgumentException("creator not found: " + dto.getCreatorId()));
				courseRepository.save(dto.toEntity(creator));
			}
		}
	}

	private CreatorListDto readCreatorData() throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
			if (is == null) {
				throw new IllegalStateException("data.json not found");
			}

			return objectMapper.readValue(is, CreatorListDto.class);
		}
	}

	private CourseListDto readCourseData() throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
			if (is == null) {
				throw new IllegalStateException("data.json not found");
			}

			return objectMapper.readValue(is, CourseListDto.class);
		}
	}
}
