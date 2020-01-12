package com.arit.adserve.ebay;

import com.arit.adserve.entity.Item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class EbayItem extends Item {

	private String saleCondition;
	
	public EbayItem(String itemId, String name, String title, int price, String saleCondition) {
		super(itemId, name, title);
		setPrice(price); 
		this.saleCondition = saleCondition;			
	}	
}
