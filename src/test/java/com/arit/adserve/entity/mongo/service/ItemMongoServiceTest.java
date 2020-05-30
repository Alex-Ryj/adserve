package com.arit.adserve.entity.mongo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.mongo.ItemMongo;
import com.arit.adserve.entity.mongo.repository.ItemMongoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ItemMongoServiceTest {

	@Autowired
	ItemMongoService service;

	@Autowired
	ItemMongoRepository repo;

	static String itemId = null;
	static String providerItemId = "id";
	static String localProviderItemId = "otherId";
	static String localProviderName = "local_provider";

	private static boolean setUpIsDone = false;
	

	@BeforeEach
	private void setUp() {
		if (setUpIsDone)
			return;
		repo.deleteAll();
		for (int i = 0; i < 30; i++) {
			ItemMongo item = new ItemMongo();		
			item.setProviderItemId(providerItemId + i);
			item.setTitle("title" + i);
			item.setProviderName(Constants.EBAY);
			item.setViewItemURL("http://viewItemURL.com" + i);
			item = service.save(item);			
		}
		
		ItemMongo item2 = new ItemMongo();

		item2.setProviderItemId(localProviderItemId);
		item2.setProviderName(localProviderName);
		item2.setTitle("title1");
		item2.setViewItemURL("http://viewItemURL.com");
		item2 = service.save(item2);
		itemId = item2.getId();
		setUpIsDone = true;
	}


	@Test
	void testFindById() {
		assertTrue(service.findById(itemId).isPresent());
	}

	@Test
	void testFindAll() {
		assertEquals(10, service.findAll(1, 10).size());
	}

	@Test
	void testFindAllById() {
		assertTrue(service.findAllById(Arrays.asList(itemId)).iterator().hasNext());
	}

	@Test
	void testCount() {
		assertEquals(31, service.count());
	}

	@Test
	void testCountItemsUpdatedAfter() {
		LocalDate localDate = LocalDate.now().minusDays(1);
		Date date = java.util.Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		assertEquals(30, service.countItemsUpdatedAfter(date, Constants.EBAY));
	}

	@Test
	void testHasImage() {
		assertFalse(service.hasImage(localProviderItemId, localProviderName));
	}

	@Test
	void testGetItemsFromProviderBefore() {
		List<ItemMongo> result = service.getItemsFromProviderBefore(new Date(), Constants.EBAY, 10);
		assertEquals(10, result.size());
	}
	
	@Test
	void testFindAllByProviderIds() throws Exception {
		assertTrue(service.findAllByProviderIds(Arrays.asList(localProviderItemId), localProviderName).iterator().hasNext());		 
	}
	
	@Test
	void testFindByProviderId() throws Exception {
		ItemMongo item = service.findByProviderId(localProviderItemId, Constants.EBAY);
		assertNotNull(item);
	}
	
	@Test
	void testFindByProviderIdNotFound() throws Exception {
		ItemMongo item = service.findByProviderId("non_exisitng_id", Constants.EBAY);
		assertNull(item);
	}
	
	@Test
	void testFindAllByProvider() throws Exception {
		PageRequest reqSorted = PageRequest.of(1, 2, Sort.by("title").descending());
		var items = service.findAllByProvider(Constants.EBAY, reqSorted);
		assertEquals(2, items.size());
		for (ItemMongo item : items) {
			log.info(item.getTitle());
		}
		 reqSorted = PageRequest.of(2, 2, Sort.by("title").descending());
		items = service.findAllByProvider(Constants.EBAY, reqSorted);
		assertEquals(2, items.size());
		for (ItemMongo item : items) {
			log.info(item.getTitle());
		}
	}


}
