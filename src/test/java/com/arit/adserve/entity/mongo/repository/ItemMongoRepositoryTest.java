package com.arit.adserve.entity.mongo.repository;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.mongo.ItemMongo;

/**
 * @author Alex Ryjoukhine
 * @since May 15, 2020
 * 
 */
@SpringBootTest
public class ItemMongoRepositoryTest {
	
	@Autowired
	ItemMongoRepository itemRepository;

	@Test
	public void testFindItemById() {
		ItemMongo item = new ItemMongo();
		item.setProviderItemId("id");
		item.setProviderName(Constants.EBAY);
		item.setTitle("title");
		item.setViewItemURL("viewItemURL");
		itemRepository.save(item);
		assertNotNull(itemRepository.findAll());
		
	}

}
