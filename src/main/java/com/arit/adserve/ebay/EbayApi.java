package com.arit.adserve.ebay;

import java.util.HashMap;
import java.util.Map;

import com.arit.adserve.comm.IApiCall;

public class EbayApi implements IApiCall {

	Map<String, String> endpoints = new HashMap<String, String>(); 
	
	public EbayApi() {
		endpoints.put("Finding", "https://svcs.ebay.com/services/search/FindingService/v1?");
		endpoints.put("Shopping", "http://open.api.ebay.com/shopping?");
		endpoints.put("SOAP", "https://api.ebay.com/wsapi");
	}
		

	@Override
	public String callApi(String url, String payload) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApiUrl(Map params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsToWait() {
		// TODO Auto-generated method stub
		return false;
	}

}
