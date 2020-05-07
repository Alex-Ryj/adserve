package com.arit.adserve.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	private String title;
	@NonNull
	private String viewItemURL;
	@Column
	@ElementCollection(targetClass=String.class)
	private List<String> subTitles;
	private String description, source, productId, galeryURL, 
				   location, country, condition, priceForamtted, currency;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String image64BaseStr;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String modifiedImage64BaseStr;
	private int price;
	private boolean process;	
	private Date createdOn = new Date(), updatedOn = new Date();
	
}
