package com.arit.adserve.providers.ebay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.arit.adserve.rules.Evaluate;

/**
 * Service to manage eBay Finding API requests (5,000 API calls per day limit for starters. See <a href="https://developer.ebay.com/support/api-call-limits">eBay call limits</a>)
 * GetMultipleItems allows 20 ItemIds in one call
 * the search can is done with key words list and paginated, every response can return up to 100 items per page
 * the max number of items <b>itemMaxRequired</b> and how frequently the items need to be updated <b>updatePeriodHours</b>
 * should be provided from the properties or other sources
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
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
    
    /**
     * eBay URL for Finding API
     */
    public static final String REQ_FINDING = "https4://svcs.ebay.com/services/search/FindingService/v1?";
    /**
     * eBay URL for shopping API
     */
    public static final String REQ_SHOPPING = "http4://open.api.ebay.com/shopping?";
    /**
     * eBay URL for soap API
     */
    public static final String REQ_SOAP = "https4://api.ebay.com/wsapi";
    /**
     * Agenda name in Drools for processing eBay finding request
     */
    public static final String RULES_EBAY_REQUEST_AGENDA = "ebayRequest";

    private Map<String, String> itemFilters;

    private Map<String, String> aspectFilters;
    

    private static final EBayFindRequest eBayFindRequest = new EBayFindRequest();
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private Evaluate evaluate;

    /**
     * @return a 'next' page URL request 
     * @throws UnsupportedEncodingException
     */
    public String getFindRequestUrl() throws UnsupportedEncodingException {
    	eBayFindRequest.setItemsMaxRequired(itemMaxRequired);
    	eBayFindRequest.setItemsTotal(itemService.count());
    	LocalDateTime localDate = LocalDateTime.now().minusHours(updatePeriodHours);
		Date date = java.util.Date.from(localDate
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
    	eBayFindRequest.setItemsUpdatedToday(itemService.countItemsUpdatedAfter(date));
        return REQ_FINDING + getParamsFindByKeywords(eBayFindRequest);
    }

    /**
     * @param eBayFindRequest
     * @return canonical query string for eBay find request
     * @throws UnsupportedEncodingException
     */
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
    
    /**
     * evaluates {@link EBayRequestService} in the rule engine to determine what is 
     * the nest state of request
     * @return {@link RequestState}
     * @throws IOException
     */
    public RequestState getRequestState() throws IOException {
    	evaluate.evaluate(eBayFindRequest, RULES_EBAY_REQUEST_AGENDA);
    	return eBayFindRequest.getState();
    }

	
	/**
	 * to update {@link EBayFindRequest} from eBay response 
	 * @param pageNumber 
	 * @param itemsPerPage
	 * @param itemsTotalInRequest
	 * @param pagesTotal
	 */
	public void updateEbayFindRequest(long pageNumber, long itemsPerPage, long itemsTotalInRequest, long pagesTotal) {
		eBayFindRequest.setPageNumber(pageNumber);
		eBayFindRequest.setItemsPerPage(itemsPerPage);
		eBayFindRequest.setItemsTotalInRequest(itemsTotalInRequest);
		eBayFindRequest.setPagesTotal(pagesTotal);		
	}
	
	public List<String> getSearchKeyWords() {
		return searchKeyWords;
	}

	public void setSearchKeyWords(List<String> searchKeyWords) {
		this.searchKeyWords = searchKeyWords;
	}
}
