package com.arit.adserve.entity.mongo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.NumericField;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.arit.adserve.entity.ItemId;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ToString
@Indexed
@Document
@CompoundIndexes({
    @CompoundIndex(name = "provider_itemId", def = "{'providerName' : 1, 'providerItemId': 1}")
})
public class ItemMongo {	
	
	@Id
	private String id;
	
	@NotBlank(message = "providerItemId is mandatory")
	private String providerItemId;
	
	@NotBlank(message = "providerName is mandatory")
	private String providerName;
	
	private Integer relevance;
	@DocumentId(name = "item_doc_id")
	private String docId;
	@org.hibernate.search.annotations.Field
	@NotBlank(message = "title is mandatory")
	private String title;
	@NotBlank(message = "viewItemURL is mandatory")
	@Size(min = 15)
	private String viewItemURL;
	
	private Set<String> subTitles = new HashSet<>();
	@org.hibernate.search.annotations.Field
	private String description;
	@org.hibernate.search.annotations.Field
	private String location;
	@org.hibernate.search.annotations.Field
	private String country;
	@org.hibernate.search.annotations.Field
	private String condition;
	@org.hibernate.search.annotations.Field
	private String currency;
	@org.hibernate.search.annotations.Field @org.hibernate.search.annotations.NumericField
	private int price;  //the price in cents, pence, kopecks etc.
	private String source;
	private String productId;
	private String galleryURL;
	private String priceFormatted;
	
	private String image64BaseStr;
	
	private String modifiedImage64BaseStr;
	private Date modifiedImageDate;	
	private boolean process;
	private boolean deleted;
	private int rank;
	private Date createdOn = new Date();
	private Date updatedOn = new Date();
	
}
