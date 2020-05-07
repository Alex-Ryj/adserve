package com.arit.adserve.providers.ebay;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.providers.IApiCall;

/**
 * Service to manage eBay Finding API requests (5,000 API calls per day limit for starters)
 * GetMultipleItems allows 20 ItemIds in one call
 * the search can is done with key words list and paginated , every response can return up to 100 items
 *
 */
@Service
public class EBayRequestService implements IApiCall {

    @Value("${EBAY_APP_ID}")
    private String ebayAppId;

    @Value("${EBAY_GLOBAL_ID}")
    private String ebayGlobalId;

    @Value("${EBAY_SITE_ID}")
    private String ebaySiteId;

    @Value("${ebay.search.keywords.list}")
    private List<String> searchKeyWords;
    
    @Value("${ebay.update.period.hours}")
    private long updatePeriodHours;
    
    @Value("${ebay.items.max.required}")
    private long itemMaxRequired;
    
    public static final String reqFinding = "https4://svcs.ebay.com/services/search/FindingService/v1?";
    public static final String reqShopping = "http4://open.api.ebay.com/shopping?";
    public static final String reqSOAP = "https4://api.ebay.com/wsapi";

    private Map<String, String> itemFilters;

    private Map<String, String> aspectFilters;
    

    private static final EBayFindRequest eBayFindRequest = new EBayFindRequest();
    
    @Autowired
    private ItemService itemService;

    public String getFindRequestUrl() {
    	eBayFindRequest.setItemsTotal(itemService.count());
    	LocalDateTime localDate = LocalDateTime.now().minusHours(updatePeriodHours);
		Date date = java.util.Date.from(localDate
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
    	eBayFindRequest.setItemsUpdatedToday(itemService.countItemsUodatedAfter(date));
        return null;
    }

    public String getParamsFindByKeywords(EBayFindRequest eBayFindRequest) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        params.put("keywords", eBayFindRequest.getSearchWords());
        params.put("paginationInput.entriesPerPage", eBayFindRequest.getItemsPerPage() + ""); 
        params.put("paginationInput.pageNumber", eBayFindRequest.getPageNumber() + "");  
        params.put("SECURITY-APPNAME", ebayAppId);
        params.put("SERVICE-VERSION", "1.0.0");
        params.put("GLOBAL-ID", ebayGlobalId);
        params.put("siteid", ebaySiteId);
        params.put("RESPONSE-DATA-FORMAT", "JSON");
        params.put("Content-Type", "text/xml;charset=utf-8");
        params.put("OPERATION-NAME", "findItemsByKeywords");     
        return IApiCall.canonicalQueryString(params);
    }

	public List<String> getSearchKeyWords() {
		return searchKeyWords;
	}

	public void setSearchKeyWords(List<String> searchKeyWords) {
		this.searchKeyWords = searchKeyWords;
	}
	
	public void updateEbayFindRequest(int itemsPerPage, int itemsTotalInRequest, int pagesTotal) {
		eBayFindRequest.setItemsPerPage(itemsPerPage);
		eBayFindRequest.setItemsTotalInRequest(itemsTotalInRequest);
		eBayFindRequest.setPagesTotal(pagesTotal);		
	}
}
