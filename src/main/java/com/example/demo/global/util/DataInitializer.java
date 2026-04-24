package com.example.demo.global.util;

import com.example.demo.domain.creator.dto.CreatorDto;
import com.example.demo.domain.creator.dto.CreatorListDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private static final String DATA_FILE_PATH = "data.json";

	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) {
		List<CreatorDto> creators = loadCreators();
		log.info("Loaded {} creators from {}", creators.size(), DATA_FILE_PATH);
	}

	public List<CreatorDto> loadCreators() {
		try (InputStream inputStream = new ClassPathResource(DATA_FILE_PATH).getInputStream()) {
			CreatorListDto creatorListDto = objectMapper.readValue(inputStream, CreatorListDto.class);
			return creatorListDto.getCreators() == null ? Collections.emptyList() : creatorListDto.getCreators();
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to load creators from " + DATA_FILE_PATH, exception);
		}
	}
}
