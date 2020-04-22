package com.arit.adserve.providers.ebay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EBayFindRequest implements Serializable {

    private static Map<String, String> endpoints = new HashMap<>();
    private int pageNumber;
    private int totalPages;
    private String serachWords;
    private int totalUpdatedItems;


    public enum RequestState {
        RETRIEVE_ITEMS,
        UPDATE_ITEMS
    }

    public enum Operations {

    }



    static {
        endpoints.put("Finding", "https4://svcs.ebay.com/services/search/FindingService/v1?");
        endpoints.put("Shopping", "http4://open.api.ebay.com/shopping?");
        endpoints.put("SOAP", "https4://api.ebay.com/wsapi");
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
