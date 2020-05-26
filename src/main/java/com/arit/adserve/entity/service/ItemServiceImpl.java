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
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemRepository itemRepository;

	@Override
	public Item findById(ItemId id) {
		return itemRepository.findById(id).orElse(null);
	}

	@Override
	public List<Item> findAll(int pageNumber, int rowsPerPage) {
		List<Item> items = new ArrayList<>();
		PageRequest sortedByLastUpdateDesc = PageRequest.of(pageNumber - 1, rowsPerPage, Sort.by("title").descending());
		itemRepository.findAll(sortedByLastUpdateDesc).forEach(items::add);
		return items;
	}

	@Override
	public Iterable<Item> findAllById(List<ItemId> itemIds) {
		return itemRepository.findAllById(itemIds);
	}

	@Override
	public Item save(Item item) {	
		return itemRepository.save(item);
	}

	@Override
	public void update(Item item) {	
		itemRepository.save(item);
	}
	
	@Override
	public void updateAll(Iterable<Item> items) {	
		itemRepository.saveAll(items);
	}

	@Override
	public void deleteById(ItemId id) {		
			itemRepository.deleteById(id);		
	}

	@Override
	public Long count() {
		return itemRepository.count();
	}

	@Override
	public Long countItemsUpdatedAfter(Date date, String providerName) {
		return itemRepository.countItemsUpdatedAfter(date, providerName);
	}

	@Override
	public boolean existsById(ItemId id) {
		return itemRepository.existsById(id);
	}
	
	@Override
	public boolean hasImage(ItemId id) {		
		return StringUtils.isNotEmpty(findById(id).getImage64BaseStr());
	}
	
	@Override
	public List<Item> getItemsFromProviderBefore(Date date, String providerName, int limit) {
		return itemRepository.getItemsFromProviderUpdatedBefore(date, providerName, PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "updatedOn")));
	}

}