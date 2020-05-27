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

	static String itemId1 = null;
	static String itemId2 = null;
	static String providerItemId1 = "id1";
	static String providerItemId2 = "id2";

	private static boolean setUpIsDone = false;
	

	@BeforeEach
	public void setUp() {
		if (setUpIsDone)
			return;
		repo.deleteAll();
		ItemMongo item1 = new ItemMongo();		
		item1.setProviderItemId(providerItemId1);
		item1.setTitle("title");
		item1.setProviderName(Constants.EBAY);
		item1.setViewItemURL("http://viewItemURL.com");
		item1 = service.save(item1);
		itemId1 = item1.getId();
		log.info("item id: {}", itemId1);
		ItemMongo item2 = new ItemMongo();

		item2.setProviderItemId(providerItemId2);
		item2.setProviderName(Constants.EBAY);
		item2.setTitle("title1");
		item2.setViewItemURL("http://viewItemURL.com");
		item2 = service.save(item2);
		itemId2 = item2.getId();
		setUpIsDone = true;
	}

	@Test
	void testFindById() {
		assertTrue(service.findById(itemId1).isPresent());
	}

	@Test
	void testFindAll() {
		assertEquals(2, service.findAll(1, 10).size());
	}

	@Test
	void testFindAllById() {
		assertTrue(service.findAllById(Arrays.asList(itemId1, itemId2)).iterator().hasNext());
	}

	@Test
	void testCount() {
		assertEquals(2, service.count());
	}

	@Test
	void testCountItemsUpdatedAfter() {
		LocalDate localDate = LocalDate.now().minusDays(1);
		Date date = java.util.Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		assertEquals(2, service.countItemsUpdatedAfter(date, Constants.EBAY));
	}

	@Test
	void testHasImage() {
		assertFalse(service.hasImage(providerItemId1, Constants.EBAY));
	}

	@Test
	void testGetItemsFromProviderBefore() {
		List<ItemMongo> result = service.getItemsFromProviderBefore(new Date(), Constants.EBAY, 10);
		assertEquals(2, result.size());
	}
	
	@Test
	void findAllByProviderIds() throws Exception {
		assertTrue(service.findAllByProviderIds(Arrays.asList(providerItemId1), Constants.EBAY).iterator().hasNext());
		 
	}
	
	@Test
	void findByProviderId() throws Exception {
		ItemMongo item = service.findByProviderId(providerItemId1, Constants.EBAY);
		assertNotNull(item);
	}

}
