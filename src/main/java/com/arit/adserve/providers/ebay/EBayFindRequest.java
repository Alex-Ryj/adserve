package com.arit.adserve.providers.ebay;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class EBayFindRequest implements Serializable {
  
	private static final long serialVersionUID = 1L;
	
    private long pageNumber = 1l;
    private long pagesTotal;
    private long itemsPerPage;
    private long itemsTotalInRequest;
    private String searchWords; 
    private long itemsMaxRequired;
    private long itemsUpdatedToday;
    private long itemsTotal;
    private RequestState state;
    private Date lastItemUpdate;   

}
