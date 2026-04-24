package com.example.demo.global.util;

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
	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
			if (is == null) {
				throw new IllegalStateException("data.json not found");
			}

			CreatorListDto data = objectMapper.readValue(is, CreatorListDto.class);

			for (CreatorDto dto : data.getCreators()) {
				if (!creatorRepository.existsByExternalId(dto.getId())) {
					creatorRepository.save(dto.toEntity());
				}
			}
		}
	}
}
