package com.arit.adserve.providers.ebay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:app.properties")
@SpringBootTest
public class EBayFindRequestServiceTest {
	
	@Autowired
	EBayRequestService service;
	
	@Test
	public void testGetListProps() throws Exception {
		
		for (String search: service.getSearchKeyWords()) {
			log.info(search);			
		}
		
	}
}