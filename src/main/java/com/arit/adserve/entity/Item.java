package com.arit.adserve.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Item entity from a provider e.g. eBay
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Entity
@Table(name="item")
@IdClass(ItemId.class)
public class Item {	
	@Id	
	@NotBlank(message = "providerItemId is mandatory")
	private String providerItemId;
	@Id	
	@NotBlank(message = "providerName is mandatory")
	private String providerName;
	@Version
	private Integer version;
	private Integer relevance;
	@NotBlank(message = "title is mandatory")
	private String title;
	@NotBlank(message = "viewItemURL is mandatory")
	@Size(min = 15)
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
	private int rank;
	private Date createdOn = new Date(), updatedOn = new Date();	
}
