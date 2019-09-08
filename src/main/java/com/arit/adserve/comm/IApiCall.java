package com.arit.adserve.comm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public interface IApiCall {

	String callApi(String url, String payload);	
	String getApiUrl(Map params);	
	boolean needsToWait();
	
	public static String canonicalQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        // params can be sorted in alphabetical order
		Map<String, String> sortedMap = new TreeMap<>();
		sortedMap.putAll(params);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : sortedMap.entrySet()) {
			sb.append(entry.getKey() + '=' + URLEncoder.encode(entry.getValue(), "UTF-8") + "&");
		}
		sb.deleteCharAt(sb.length()-1);
        return sb.toString();
	}
	
}
