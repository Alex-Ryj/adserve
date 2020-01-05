package com.arit.adserve.ebay;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.IApiCall;

@Service
public class EbayApi  extends RouteBuilder implements IApiCall{

	Map<String, String> endpoints = new HashMap<String, String>(); 
		
	@Value("${EBAY_APP_ID}")
	private String ebayAppId;
	
	@Value("${EBAY_GLOBAL_ID}")
	private String ebayGlobalId;
	
	@Value("${EBAY_SITE_ID}")
	private String ebaySiteId;	
	
	@Value("${api.path}")
	private String contextPath;
	
	@Value("${api.port}")
	private int serverPort;
	
	
	
	public EbayApi() {
		endpoints.put("Finding", "https4://svcs.ebay.com/services/search/FindingService/v1?");
		endpoints.put("Shopping", "http4://open.api.ebay.com/shopping?");
		endpoints.put("SOAP", "https4://api.ebay.com/wsapi");
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


	@Override
	public void configure() throws Exception {
		restConfiguration()
		  .contextPath(contextPath) 
		  .port(serverPort)
		  .enableCORS(true)
		  .apiContextPath("/api-doc")
		  .apiProperty("api.title", "Test REST API")
		  .apiProperty("api.version", "v1")
		  .apiContextRouteId("doc-api")
		  .component("servlet")
		  .bindingMode(RestBindingMode.json);
		
	
		
		rest("/test/")
		  .id("test-route")
		  .get("test")		  
		  .to("direct:remoteService");
		
		from("timer://foo?fixedRate=true&delay=0&period=10000")
		  .to("direct:remoteService")		  
		  .to("file:target/google?fileName=message.html");
		
		from("direct:remoteService")   
		  .to(endpoints.get("Finding")+getParams());

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
		params.put("paginationInput.entriesPerPage", "10");
		return IApiCall.canonicalQueryString(params);
		
	}

}
