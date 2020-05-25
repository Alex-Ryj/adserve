package com.arit.adserve.entity.mongo.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.mongo.ItemMongo;

/**
 * @author Alex Ryjoukhine
 * @since May 25, 2020
 */
public interface ItemMongoRepository extends MongoRepository<ItemMongo, ItemId>  {



}
