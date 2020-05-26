package com.arit.adserve.entity.mongo.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.arit.adserve.entity.mongo.ItemMongo;

/**
 * @author Alex Ryjoukhine
 * @since May 25, 2020
 */
@Repository
public interface ItemMongoRepository extends MongoRepository<ItemMongo, String>  {



}
