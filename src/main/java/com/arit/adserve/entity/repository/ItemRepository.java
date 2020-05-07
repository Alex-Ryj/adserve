package com.arit.adserve.entity.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.arit.adserve.entity.Item;

@Repository
public interface ItemRepository extends PagingAndSortingRepository<Item, String> {
	
	@Query("SELECT count(item) FROM Item item WHERE item.updatedOn >= ?1")
	public long countItemsUpdatedAfter(Date date);

}
