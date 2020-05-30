package com.arit.adserve.entity.mongo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.arit.adserve.entity.mongo.ItemMongo;
import com.arit.adserve.entity.mongo.repository.ItemMongoRepository;

/**
 * @author Alex Ryjoukhine
 * @since May 26, 2020
 */
@Service
public class ItemMongoService {

	/** ItemMongo fields */
	private static final String DELETED = "deleted";
	private static final String TITLE = "title";
	private static final String UPDATED_ON = "updatedOn";
	private static final String PROVIDER_NAME = "providerName";
	private static final String PROVIDER_ITEM_ID = "providerItemId";

	@Autowired
	private MongoOperations mongoOps;

	@Autowired
	private ItemMongoRepository repo;	
	
	

	public Optional<ItemMongo> findById(String id) {
		return repo.findById(id);
	}
	
	public List<ItemMongo> findAll(int pageNumber, int rowsPerPage) {
		List<ItemMongo> items = new ArrayList<>();
		PageRequest sortedByLastUpdateDesc = PageRequest.of(pageNumber - 1, rowsPerPage, Sort.by(UPDATED_ON).descending());
		repo.findAll(sortedByLastUpdateDesc).forEach(items::add);
		return items;
	}
	
	public List<ItemMongo> findAllByProvider(String providerName, PageRequest pageRequest) {
		List<ItemMongo> items = new ArrayList<>();
		
//		ItemMongo exampleItem = new ItemMongo();
//		exampleItem.setProviderName(providerName);
//		Example<ItemMongo> example = Example.of(exampleItem);
		repo.findAllByProviderName(providerName, pageRequest).forEach(items::add);	
		return items;
	}
	
	public ItemMongo findByProviderId(String providerItemId, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where(PROVIDER_ITEM_ID).is(providerItemId).and(PROVIDER_NAME).is(providerName));				
		return mongoOps.findOne(query, ItemMongo.class);
	}

	public Iterable<ItemMongo> findAllById(List<String> itemIds) {
		return repo.findAllById(itemIds);
	}
	
	public Iterable<ItemMongo> findAllByProviderIds(List<String> providerItemIds, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where(PROVIDER_NAME).is(providerName).and(PROVIDER_ITEM_ID).in(providerItemIds));				
		return mongoOps.find(query, ItemMongo.class);
	}

	public ItemMongo save(ItemMongo item) {
		return repo.save(item);
	}


	public void updateAll(Iterable<ItemMongo> items) {
		repo.saveAll(items);
	}

	public void deleteById(String id) {
		repo.deleteById(id);
	}

	public Long count() {
		return repo.count();
	}
	
	public Long countByProvider(String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where(PROVIDER_NAME).is(providerName));				
		return mongoOps.count(query, ItemMongo.class);
	}

	public Long countItemsUpdatedAfter(Date date, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where(UPDATED_ON).gte(date).and(PROVIDER_NAME).is(providerName).and(DELETED).is(false));				
		return mongoOps.count(query, ItemMongo.class);
	}

	public boolean hasImage(String providerItemId, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where(PROVIDER_ITEM_ID).is(providerItemId).and(PROVIDER_NAME).is(providerName));
		var item = findByProviderId(providerItemId, providerName);
		return item != null && StringUtils.isNotEmpty(item.getImage64BaseStr());
	}

	public List<ItemMongo> getItemsFromProviderBefore(Date date, String providerName, int limit) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where(UPDATED_ON).lte(date).and(PROVIDER_NAME).is(providerName).and(DELETED).is(false))
				.limit(limit).with(Sort.by(UPDATED_ON).descending());
		return mongoOps.find(query, ItemMongo.class);
	}
}
