package com.arit.adserve.entity.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.repository.ItemRepository;

/**
 * {@link Item} persistence service
 * 
 * @author Alex Ryjoukhine
 * @since May 12, 2020
 * 
 */
@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public Item findById(ItemId id) {
		return itemRepository.findById(id).orElse(null);
	}

	public List<Item> findAll(int pageNumber, int rowPerPage) {
		List<Item> items = new ArrayList<>();
		PageRequest sortedByLastUpdateDesc = PageRequest.of(pageNumber - 1, rowPerPage, Sort.by("title").descending());
		itemRepository.findAll(sortedByLastUpdateDesc).forEach(items::add);
		return items;
	}

	public Iterable<Item> findAllById(List<ItemId> itemIds) {
		return itemRepository.findAllById(itemIds);
	}

	public Item save(Item item) {	
		return itemRepository.save(item);
	}

	public void update(Item item) {	
		itemRepository.save(item);
	}
	
	public void updateAll(Iterable<Item> items) {	
		itemRepository.saveAll(items);
	}

	public void deleteById(ItemId id) {		
			itemRepository.deleteById(id);		
	}

	public Long count() {
		return itemRepository.count();
	}

	public Long countItemsUpdatedAfter(Date date, String providerName) {
		return itemRepository.countItemsUpdatedAfter(date, providerName);
	}

	public boolean existsById(ItemId id) {
		return itemRepository.existsById(id);
	}
	
	public boolean hasImage(ItemId id) {		
		return StringUtils.isNotEmpty(findById(id).getImage64BaseStr());
	}
	
	public List<Item> getItemsFromProviderBefore(Date date, String providerName, int limit) {
		return itemRepository.getItemsFromProviderUpdatedBefore(date, providerName, PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "updatedOn")));
	}

}
