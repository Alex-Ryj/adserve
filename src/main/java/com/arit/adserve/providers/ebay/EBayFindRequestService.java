package com.arit.adserve.providers.ebay;

import com.arit.adserve.providers.IApiCall;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to manage eBay Finding API requests (5,000 API calls per day limit for starters)
 * GetMultipleItems allows 20 ItemIds in one call
 * the search can is done with key words list and paginated , every response can return up to 100 items
 *
 */
@Service
public class EBayFindRequestService implements IApiCall {

    @Value("${EBAY_APP_ID}")
    private String ebayAppId;

    @Value("${EBAY_GLOBAL_ID}")
    private String ebayGlobalId;

    @Value("${EBAY_SITE_ID}")
    private String ebaySiteId;

    @Value("${ebay.search.keywords.list}")
    private List<String> searchKeyWords;

    private Map<String, String> itemFilters;

    private Map<String, String> aspectFilters;

    private static EBayFindRequest eBayFindRequest;

    public String getRequestUrl() {
        return null;
    }

    private String getParams() throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        params.put("SECURITY-APPNAME", ebayAppId);
        params.put("SERVICE-VERSION", "1.0.0");
        params.put("GLOBAL-ID", ebayGlobalId);
        params.put("siteid", ebaySiteId);
        params.put("RESPONSE-DATA-FORMAT", "JSON");
        params.put("Content-Type", "text/xml;charset=utf-8");
        params.put("OPERATION-NAME", "findItemsByKeywords");
        params.put("keywords", "drone");
        params.put("paginationInput.entriesPerPage", "100");
        return IApiCall.canonicalQueryString(params);
    }

    public String getEbayAppId() {
        return ebayAppId;
    }

    public void setEbayAppId(String ebayAppId) {
        this.ebayAppId = ebayAppId;
    }

    public String getEbayGlobalId() {
        return ebayGlobalId;
    }

    public void setEbayGlobalId(String ebayGlobalId) {
        this.ebayGlobalId = ebayGlobalId;
    }

    public String getEbaySiteId() {
        return ebaySiteId;
    }

    public void setEbaySiteId(String ebaySiteId) {
        this.ebaySiteId = ebaySiteId;
    }
}
