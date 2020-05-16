package com.arit.adserve.entity.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;

/**
 * @author Alex Ryjoukhine
 * @since May 15, 2020
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("persistence-test.yml")
public class ItemRepositoryTest {
	
	@Autowired
	ItemRepository itemRepository;

	@Test
	public void testFindItemById() {
		Item item = new Item();
		item.setProviderItemId("id");
		item.setProviderName(Constants.EBAY);
		item.setTitle("title");
		item.setViewItemURL("viewItemURL");
		itemRepository.save(item);
//		assertNotNull(itemRepository.findById("Id").get());
//		assertFalse(itemRepository.findById("notId").isPresent());
		
	}

}
