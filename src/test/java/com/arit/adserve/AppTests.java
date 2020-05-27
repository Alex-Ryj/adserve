package com.arit.adserve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class AppTests {
	
	@Value("${spring.jpa.properties.hibernate.search.default.indexBase}")
	private String mongodbUrl;

	@Test
	void contextLoads() {
		log.info("mongodbUrl: " + mongodbUrl);
	}

}
