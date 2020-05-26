package com.arit.adserve.entity.service;

import java.util.Date;
import java.util.List;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;

public interface ItemService {

	Item findById(ItemId id);

	List<Item> findAll(int pageNumber, int rowsPerPage);

	Iterable<Item> findAllById(List<ItemId> itemIds);

	Item save(Item item);

	void update(Item item);

	void updateAll(Iterable<Item> items);

	void deleteById(ItemId id);

	Long count();

	Long countItemsUpdatedAfter(Date date, String providerName);

	boolean existsById(ItemId id);

	boolean hasImage(ItemId id);

	List<Item> getItemsFromProviderBefore(Date date, String providerName, int limit);

}