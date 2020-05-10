package com.arit.adserve.entity.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.repository.ItemRepository;

@Service
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

	public void deleteById(ItemId id) {		
			itemRepository.deleteById(id);		
	}

	public Long count() {
		return itemRepository.count();
	}

	public Long countItemsUodatedAfter(Date date) {
		return itemRepository.countItemsUpdatedAfter(date);
	}

	public boolean existsById(ItemId id) {
		return itemRepository.existsById(id);
	}

}
