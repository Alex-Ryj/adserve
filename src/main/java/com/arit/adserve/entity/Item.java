package com.arit.adserve.entity;

public class Item {

	private String name;
	private int price;
	private boolean process;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public boolean isProcess() {
		return process;
	}
	public void setProcess(boolean process) {
		this.process = process;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
