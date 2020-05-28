package com.arit.adserve.entity.mongo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private MongoOperations mongoOps;

	@Autowired
	private ItemMongoRepository repo;

	public Optional<ItemMongo> findById(String id) {
		return repo.findById(id);
	}
	
	public List<ItemMongo> findAll(int pageNumber, int rowsPerPage) {
		List<ItemMongo> items = new ArrayList<>();
		PageRequest sortedByLastUpdateDesc = PageRequest.of(pageNumber - 1, rowsPerPage, Sort.by("title").descending());
		repo.findAll(sortedByLastUpdateDesc).forEach(items::add);
		return items;
	}
	
	public ItemMongo findByProviderId(String providerItemId, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("providerItemId").is(providerItemId).and("providerName").is(providerName));				
		return mongoOps.findOne(query, ItemMongo.class);
	}

	public Iterable<ItemMongo> findAllById(List<String> itemIds) {
		return repo.findAllById(itemIds);
	}
	
	public Iterable<ItemMongo> findAllByProviderIds(List<String> providerItemIds, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("providerName").is(providerName).and("providerItemId").in(providerItemIds));				
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

	public Long countItemsUpdatedAfter(Date date, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("updatedOn").gte(date).and("providerName").is(providerName).and("deleted").is(false));				
		return mongoOps.count(query, ItemMongo.class);
	}

	public boolean hasImage(String providerItemId, String providerName) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("providerItemId").is(providerItemId).and("providerName").is(providerName));
		var item = findByProviderId(providerItemId, providerName);
		return StringUtils.isNotEmpty(item.getImage64BaseStr());
	}

	public List<ItemMongo> getItemsFromProviderBefore(Date date, String providerName, int limit) {
		Query query = new Query();
		query.addCriteria(
				Criteria.where("updatedOn").lte(date).and("providerName").is(providerName).and("deleted").is(false))
				.limit(limit).with(Sort.by("updatedOn").descending());
		return mongoOps.find(query, ItemMongo.class);
	}
}
