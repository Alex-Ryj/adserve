package com.arit.adserve.ebay;

import com.arit.adserve.entity.Item;

public class EbayItem extends Item{

	private String saleCondition;
	
	public EbayItem(int price, String saleCondition) {
		setPrice(price); 
		this.saleCondition = saleCondition;			
	}

	public String getSaleCondition() {
		return saleCondition;
	}
	public void setSaleCondition(String saleCondition) {
		this.saleCondition = saleCondition;
	}
}
