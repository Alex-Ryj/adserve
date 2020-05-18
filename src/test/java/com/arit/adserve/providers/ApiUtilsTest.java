/**
 * 
 */
package com.arit.adserve.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author Alex Ryjoukhine
 * @since May 13, 2020
 * 
 */
public class ApiUtilsTest {

	@Test
	public void test() throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<>();
        params.put("callname", "GetMultipleItems");
        params.put("responseencoding", "JSON"); 
        params.put("appid", "id");
        params.put("version", "967");
		assertEquals("appid=id&callname=GetMultipleItems&responseencoding=JSON&version=967", ApiUtils.canonicalQueryString(params));
	}

}
