package com.arit.adserve.entity.mongo;

import java.util.List;

import lombok.Data;

@Data
public class ItemsPage {
	
	private List<ItemMongo> items;
	private long count;
	private long pageNum;
	

}
