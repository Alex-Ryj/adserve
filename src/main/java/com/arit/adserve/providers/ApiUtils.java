package com.arit.adserve.providers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import com.arit.adserve.providers.ebay.EBayRequestService;

import lombok.extern.slf4j.Slf4j;

import java.util.TreeMap;


/**
 * @author Alex Ryjoukhine
 * @since May 15, 2020
 * 
 */
public class ApiUtils {

	
	/**
	 * @param params
	 * @return sorted params by the natural order
	 * @throws UnsupportedEncodingException
	 */
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
