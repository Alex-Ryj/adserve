package com.arit.adserve.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ItemId implements Serializable {

	private static final long serialVersionUID = 1L;
	private String providerItemId;
	private String providerName;
	
	public ItemId() {
		// TODO Auto-generated constructor stub
	}
	
	public ItemId(String providerItemId, String providerName) {
		this.providerItemId = providerItemId;
		this.providerName = providerName;				
	}
	
}
 