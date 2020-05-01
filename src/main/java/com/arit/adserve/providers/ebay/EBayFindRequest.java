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
	
	private static Map<String, String> endpoints = new HashMap<>();
    private int pageNumber;
    private int pagesTotal;
    private int itemsPerPage;
    private int itemsTotalInRequest;
    private String searchWords; 
    private int itemsMaxRequired;
    private int itemsUpdatedToday;
    private int itemsTotal;
    private RequestState state;
    private Date lastItemUpdate;

    static {
        endpoints.put("Finding", "https4://svcs.ebay.com/services/search/FindingService/v1?");
        endpoints.put("Shopping", "http4://open.api.ebay.com/shopping?");
        endpoints.put("SOAP", "https4://api.ebay.com/wsapi");
    }  

}
