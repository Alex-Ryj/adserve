package com.arit.adserve.providers.ebay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since May 13, 2020
 * 
 */
@Slf4j
@TestPropertySource(locations = {"file:app.properties"})
@SpringBootTest
public class EBayFindRequestServiceTest {
	
	@Autowired
	EBayRequestService service;
	
	@Test
	public void testGetListProps() throws Exception {		
		int i = 0;
		    service.setNextKeyWords();
		
	}
}