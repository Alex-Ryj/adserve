package com.arit.adserve.providers.ebay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.providers.ApiUtils;
import com.arit.adserve.rules.Evaluate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
public class EBayRequestService {

    @Value("${EBAY_APP_ID}")
    private String ebayAppId;

    @Value("${EBAY_GLOBAL_ID}")
    private String ebayGlobalId;

    @Value("${EBAY_SITE_ID}")
    private String ebaySiteId;

    @Value("${ebay.search.keywords.list}")
    private LinkedList<String> searchKeyWords;
    
    private AtomicInteger keywordIndex = new AtomicInteger(0);
    
    @Value("${ebay.search.items.per.page}")
    private long itemsPerPage;
    
    @Value("${ebay.update.period.hours}")
    private long updatePeriodHours;
    
    @Value("${ebay.items.max.required}")
    private long itemsMaxRequired;
    
    /**
     * eBay URL (camel https4)  for Finding API
     */
    public static final String REQ_FINDING = "https4://svcs.ebay.com/services/search/FindingService/v1?";
    /**
     * eBay URL (camel https4) for shopping API
     */
    public static final String REQ_SHOPPING = "https4://open.api.ebay.com/shopping?";
    /**
     * eBay URL (camel https4) for soap API 
     */
    public static final String REQ_SOAP = "https4://api.ebay.com/wsapi";
    /**
     * Agenda name in Drools for processing eBay finding request
     */
    public static final String RULES_EBAY_REQUEST_AGENDA = "ebayRequest";  

    private static final EBayFindRequest eBayFindRequest = new EBayFindRequest();
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private Evaluate evaluate;

    /**
     * @return a 'next' page URL request 
     * @throws UnsupportedEncodingException
     */
    public String getFindRequestQuery() throws UnsupportedEncodingException {
    	eBayFindRequest.setItemsPerPage(itemsPerPage);
    	eBayFindRequest.setItemsMaxRequired(itemsMaxRequired);
    	eBayFindRequest.setItemsTotal(itemService.count());
    	LocalDateTime localDate = LocalDateTime.now().minusHours(updatePeriodHours);
		Date date = java.util.Date.from(localDate
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
    	eBayFindRequest.setItemsUpdatedToday(itemService.countItemsUpdatedAfter(date, Constants.EBAY)); 
    	var result = getParamsFindByKeywords(eBayFindRequest);
    	log.info("calling find request: " + result);
        return result;
    }

    /**
     * @param eBayFindRequest <a href="https://developer.ebay.com/DevZone/finding/Concepts/MakingACall.html">eBay GetMultipleItems</a>
     * @return canonical query string for eBay find request
     * @throws UnsupportedEncodingException
     */
    public String getParamsFindByKeywords(EBayFindRequest eBayFindRequest) throws UnsupportedEncodingException {
    	 Map<String, String> params = new HashMap<>();
    	 if (eBayFindRequest.getSearchWords() == null) { setNextKeyWords(); }
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
         log.info("params: " + params);
                  
         log.info(ApiUtils.canonicalQueryString(params));
         return ApiUtils.canonicalQueryString(params);
    }
    
    /**
     * @param ebay Item Ids list  <a href="https://developer.ebay.com/devzone/shopping/docs/CallRef/GetMultipleItems.html#Samples">eBay GetMultipleItems</a>
     * @param ebayItemIds
     * @return canonical query string for eBay find items request
     * @throws UnsupportedEncodingException
     */
    public String getFindItemsUrl(List<String> ebayItemIds) throws UnsupportedEncodingException {
        if(ebayItemIds.isEmpty() ) throw new IllegalArgumentException("no item ids");
    	Map<String, String> params = new HashMap<>();
        params.put("callname", "GetMultipleItems");
        params.put("responseencoding", "JSON"); 
        params.put("appid", ebayAppId);
        params.put("version", "967");
        params.put("siteid", ebaySiteId);
        StringBuilder sb = new StringBuilder();
        for (String itemId : ebayItemIds) {
			sb.append(itemId + ",");
		}
        sb.setLength(sb.length() - 1);
        params.put("ItemID", sb.toString()); 
        return ApiUtils.canonicalQueryString(params);
    }
    
    /**
     * evaluates {@link EBayRequestService} in the rule engine to determine what is 
     * the nest state of request. 
     * @return {@link RequestState}
     * @throws IOException
     */
    public RequestState updateRequestState() throws IOException {
    	log.debug("find request before: {}", eBayFindRequest);
    	eBayFindRequest.setItemsMaxRequired(itemsMaxRequired);
    	evaluate.evaluate(eBayFindRequest, RULES_EBAY_REQUEST_AGENDA);
    	log.debug("find request after: {}", eBayFindRequest);
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
		eBayFindRequest.setItemsMaxRequired(itemsMaxRequired);
	}
		
	/**
	 * set the next element of searchKeyWords or first if there is no more elements 
	 * in eBayFindRequest
	 */
	public void setNextKeyWords() {
		if (keywordIndex.get() > searchKeyWords.size() - 1) {
			keywordIndex.set(0);			
			}		
		eBayFindRequest.setSearchWords(searchKeyWords.get(keywordIndex.getAndIncrement()));		
	}	
	
	/**
	 * @return the date after whci items need to be updated
	 */
	public Date getDateLimitForItems() {
		LocalDateTime date = LocalDateTime.now().minusHours(updatePeriodHours);
		return java.util.Date.from(date.atZone(ZoneId.systemDefault()).toInstant());		
	}

}
