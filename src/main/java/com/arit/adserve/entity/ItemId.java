package com.arit.adserve.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

/**
 * a composite primary key for {@link Item}
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@EqualsAndHashCode
public class ItemId implements Serializable {

	private static final long serialVersionUID = 1L;
	private String providerItemId;
	private String providerName;
	
	public ItemId() {
		// ORM required
	}
	
	public ItemId(String providerItemId, String providerName) {
		this.providerItemId = providerItemId;
		this.providerName = providerName;				
	}
	
}
 