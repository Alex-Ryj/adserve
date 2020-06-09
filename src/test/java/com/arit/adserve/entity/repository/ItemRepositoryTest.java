package com.arit.adserve.entity.repository;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.comm.SpringUtil;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;

/**
 * @author Alex Ryjoukhine
 * @since May 15, 2020
 * 
 */
@SpringBootTest
@PropertySource("persistence-test.yml")
@Validated
public class ItemRepositoryTest {
	
	@Autowired
	ItemRepository itemRepository;

	@Test
	public void testFindItemById() {
		Item item = new Item();
		item.setProviderItemId("id");
		item.setProviderName(Constants.EBAY);
		item.setTitle("title");
		item.setViewItemURL("htpp;//viewItemURL long string ");
		SpringUtil.validateInput(item);
		itemRepository.save(item);
		assertNotNull(itemRepository.findById(new ItemId("id", Constants.EBAY)));
		assertFalse(itemRepository.findById(new ItemId("not id", Constants.EBAY)).isPresent());
		
	}
	
	 

}
