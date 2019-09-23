package com.arit.adserve.ebay;

public class Item {

	private String saleCondition;
	private int price;
	boolean process;
	
	public Item(int price, String saleCondition) {
		this.price = price;
		this.saleCondition = saleCondition;
			
	}
	public boolean isProcess() {
		return process;
	}
	public void setProcess(boolean process) {
		this.process = process;
	}
	public String getSaleCondition() {
		return saleCondition;
	}
	public void setSaleCondition(String saleCondition) {
		this.saleCondition = saleCondition;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
