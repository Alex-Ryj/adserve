package com.arit.adserve.comm;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class AbstractApiCallTest {

	@Test
	public void test() throws UnsupportedEncodingException  {		
		Map<String, String> params = new HashMap<>();
		params.put("key1", "param one");
		params.put("key2", "param& two");		
		String result = IApiCall.canonicalQueryString(params);
		assertEquals("key1=param+one&key2=param%26+two", result);
	}

}
