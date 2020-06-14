package com.arit.adserve.entity.mongo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

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
	@NotBlank(message = "title is mandatory")
	private String title;
	@NotBlank(message = "viewItemURL is mandatory")
	@Size(min = 15)
	private String viewItemURL;	
	private Set<String> subTitles = new HashSet<>();
	private String description;
	private String location;
	private String country;
	private String condition;
	private String currency;
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
	
	/**
	 * @return lucene document for indexing
	 */
	public Document getLuceneDocument() {
		 Document document = new Document();
		 document.add(new StoredField("id", this.id));
		 document.add(new StringField("providerName", this.providerName, Field.Store.NO));
		 if(StringUtils.isNotBlank(this.country)) document.add(new StringField("country", this.country, Field.Store.NO));
		 if(StringUtils.isNotBlank(this.title)) document.add(new TextField("title", this.title, Field.Store.NO));
		 if(!this.subTitles.isEmpty()) document.add(new TextField("subTitles", this.subTitles.stream().collect(Collectors.joining(" ")), Field.Store.NO));
		 if(StringUtils.isNotBlank(this.description))document.add(new TextField("description", this.description, Field.Store.NO));
		 if(price>0) { document.add(new IntPoint("price", this.price)); }
         document.add(new SortedNumericDocValuesField("rank", this.rank));         
         return document;		
	}
	
}
