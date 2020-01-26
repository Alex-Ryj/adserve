package com.arit.adserve.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name="item")
@ToString
public class Item {	
	
	@Id	
	@NonNull
	private String itemId;
	@NonNull
	private String title, source;
	private String description, productId, galeryURL, viewItemURL, 
				   location, country, condition, priceForamtted, currency;
	@Lob
	private String image64BaseStr;
	@Lob
	private String modifiedImage64BaseStr;
	private int price;
	private boolean process;	
	private Date createdOn = new Date(), updatedOn = new Date();
	
}
