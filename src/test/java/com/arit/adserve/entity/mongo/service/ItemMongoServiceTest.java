package com.arit.adserve.entity.mongo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.mongo.ItemMongo;
import com.arit.adserve.entity.mongo.repository.ItemMongoRepository;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class ItemMongoServiceTest {

	
	@Autowired
	ItemMongoService service;
	
	@Autowired
	ItemMongoRepository repo;
	
	String itemId = null;
	String itemId2 = null;
	
	   private static boolean setUpIsDone = false;

	    @BeforeEach
	    public void setUp () {
	        if(setUpIsDone) return;
	        repo.deleteAll();
	        ItemMongo item = new ItemMongo();
	        item.setProviderItemId("id");
	        item.setTitle("title");
	        item.setProviderName(Constants.EBAY);
	        item.setViewItemURL("http://viewItemURL.com");
	        itemId = service.save(item).getId();
	        ItemMongo item1 = new ItemMongo();
	        item1.setProviderItemId("id1");
	        item1.setProviderName(Constants.EBAY);
	        item1.setTitle("title1");
	        item1.setViewItemURL("http://viewItemURL.com");
	        itemId2 = service.save(item1).getId();
	        setUpIsDone = true;
	    }	    

	@Test
	void testFindById() {
	 assertTrue(service.findById(itemId).isPresent());
	}

	@Test
	void testFindAll() {
		assertEquals(2, service.findAll(0, 10).size());
	}

	@Test
	void testFindAllById() {
		assertEquals(2, service.findAllById(Arrays.asList(itemId, itemId2)));
	}

	@Test
	void testCount() {
		assertEquals(2, service.count());
	}

	@Test
	void testCountItemsUpdatedAfter() {
		LocalDate localDate = LocalDate.now().minusDays(1);
		Date date = java.util.Date.from(localDate.atStartOfDay()
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
		assertEquals(2, service.countItemsUpdatedAfter(date, Constants.EBAY));
	}

	@Test
	void testHasImage() {
		fail("Not yet implemented");
	}

	@Test
	void testGetItemsFromProviderBefore() {
		fail("Not yet implemented");
	}	

	@DisplayName("given object to save" + " when save object using MongoDB template" + " then object is saved")
	@Test
	public void test(@Autowired MongoTemplate mongoTemplate) {
		// given
		DBObject objectToSave = BasicDBObjectBuilder.start().add("key", "value").get();

		// when
		mongoTemplate.save(objectToSave, "collection");

		// then
		log.info("size {}",  mongoTemplate.findAll(DBObject.class, "collection").size());
		assertTrue(mongoTemplate.findAll(DBObject.class, "collection").size() == 1);
	}

}
