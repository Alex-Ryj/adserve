package com.arit.adserve.entity;

import lombok.Data;
import lombok.NonNull;

@Data
public class Item {	
	@NonNull
	private String itemId, name, title;
	private String description, productId, galeryURL, viewItemURL, location, country, condition;
	private int price;
	private boolean process;	

}
