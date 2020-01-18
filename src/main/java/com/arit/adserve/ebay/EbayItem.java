package com.arit.adserve.ebay;

import com.arit.adserve.entity.Item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class EbayItem extends Item {
	
	public EbayItem(String itemId, String name, String title, int price, String currency) {
		super(itemId, name, title);
		setPrice(price); 
		setCurrency(currency);
	}	
}
