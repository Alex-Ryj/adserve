package com.arit.adserve.providers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public interface IApiCall {

	
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
