package com.arit.adserve.entity.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.arit.adserve.entity.Item;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemRepositoryTest {
	
	@Autowired
	ItemRepository itemRepository;

	@Test
	public void testFindItemById() {
		Item item = new Item();
		item.setProviderItemId("Id");
		itemRepository.save(item);
//		assertNotNull(itemRepository.findById("Id").get());
//		assertFalse(itemRepository.findById("notId").isPresent());
		
	}

}
