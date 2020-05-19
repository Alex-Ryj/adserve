package com.arit.adserve.entity.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;

/**
 * @author Alex Ryjoukhine
 * @since May 14, 2020
 * 
 */
@Repository
public interface ItemRepository extends PagingAndSortingRepository<Item, ItemId> {	
	
	@Query("SELECT count(item) FROM Item item WHERE item.updatedOn >= ?1 and item.providerName = ?2")
	public long countItemsUpdatedAfter(Date date, String providerName);
	
	@Query("SELECT item FROM Item item WHERE item.updatedOn <= ?1 and item.providerName = ?2 and item.deleted=FALSE")
	public List<Item> getItemsFromProviderUpdatedBefore(Date date, String providerName, Pageable pageable);
	
	@Query("SELECT item FROM Item item WHERE item.providerName = ?2 and item.deleted=FALSE ORDER BY item.updatedOn DESC")
	public List<Item> getItemsNotDeleted(String providerName, Pageable pageable);
	
//	@EntityGraph(type = EntityGraph.EntityGraphType.FETCH)
//	public Optional<Item> findById(ItemId id) ;

}
