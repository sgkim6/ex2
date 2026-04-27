package com.example.demo.global.util;

import com.example.demo.domain.cancel.dto.CancelDto;
import com.example.demo.domain.cancel.dto.CancelListDto;
import com.example.demo.domain.cancel.repository.CancelRepository;
import com.example.demo.domain.course.dto.CourseDto;
import com.example.demo.domain.course.dto.CourseListDto;
import com.example.demo.domain.course.entity.Course;
import com.example.demo.domain.course.repository.CourseRepository;
import com.example.demo.domain.creator.entity.Creator;
import com.example.demo.domain.creator.dto.CreatorDto;
import com.example.demo.domain.creator.dto.CreatorListDto;
import com.example.demo.domain.creator.repository.CreatorRepository;
import com.example.demo.domain.sale.entity.Sale;
import com.example.demo.domain.sale.dto.SaleDto;
import com.example.demo.domain.sale.dto.SaleListDto;
import com.example.demo.domain.sale.repository.SaleRepository;
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
	private final SaleRepository saleRepository;
	private final CancelRepository cancelRepository;
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

		if (saleRepository.count() == 0) {
			SaleListDto saleData = readSaleData();
			for (SaleDto dto : saleData.getSaleRecords()) {
				Course course = courseRepository.findByIdAndIsValidTrue(dto.getCourseId())
					.orElseThrow(() -> new IllegalArgumentException("course not found: " + dto.getCourseId()));
				saleRepository.save(dto.toEntity(course));
			}
		}

		if (cancelRepository.count() == 0) {
			CancelListDto cancelData = readCancelData();
			for (CancelDto dto : cancelData.getCancels()) {
				Sale sale = saleRepository.findByIdAndIsValidTrue(dto.getSaleId())
					.orElseThrow(() -> new IllegalArgumentException("sale not found: " + dto.getSaleId()));
				cancelRepository.save(dto.toEntity(sale));
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

	private SaleListDto readSaleData() throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
			if (is == null) {
				throw new IllegalStateException("data.json not found");
			}

			return objectMapper.readValue(is, SaleListDto.class);
		}
	}

	private CancelListDto readCancelData() throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
			if (is == null) {
				throw new IllegalStateException("data.json not found");
			}

			return objectMapper.readValue(is, CancelListDto.class);
		}
	}
}
