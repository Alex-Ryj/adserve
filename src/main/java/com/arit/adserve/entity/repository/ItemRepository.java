package com.arit.adserve.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.arit.adserve.entity.Item;

@Repository
public interface ItemRepository extends CrudRepository<Item, String> {

}
