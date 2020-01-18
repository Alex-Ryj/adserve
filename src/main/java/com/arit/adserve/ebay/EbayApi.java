package com.arit.adserve.ebay;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.IApiCall;
import com.arit.adserve.controller.ItemController;
import com.arit.adserve.rules.Evaluate;

@Service
public class EbayApi  extends RouteBuilder implements IApiCall {
	
	private static 	Logger logger = LoggerFactory.getLogger(EbayApi.class);

	Map<String, String> endpoints = new HashMap<>(); 
		
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
	
	@Autowired
	private EbayJsonConvert convert;	
	
	@Autowired
	private Evaluate evaluate;
	
	
	public EbayApi() {
		endpoints.put("Finding", "https4://svcs.ebay.com/services/search/FindingService/v1?");
		endpoints.put("Shopping", "http4://open.api.ebay.com/shopping?");
		endpoints.put("SOAP", "https4://api.ebay.com/wsapi");
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
		  .get("ebay")			
		  .to("direct:remoteEbayApi");

		/**
		from("timer://foo?fixedRate=true&delay=0&period=10000")
		  .to("direct:remoteEbayApi")		  
//		  .to("file:target/google?fileName=message.json")
		  .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item")
		  .bean(EbayJsonConvert.class)
		  .to("log:item");
		*/
		from("direct:remoteEbayApi")
		  .removeHeaders("CamelHttp*")
		  .to(endpoints.get("Finding")+getParams())
		  .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item")
		  .bean(convert)
		  .bean(evaluate)
		  .process(new Processor() {			
			@Override
			public void process(Exchange exchange) throws Exception {
				EbayItem item = exchange.getIn().getBody(EbayItem.class);
				logger.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());					
			}
		})
		  .to("log:item");
		
		from("direct:getImage")
		.filter().method(EbayItem.class, "isProcess")
		.setHeader("Accept", simple("image/jpeg"))
        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
		  .process(new Processor() {			
			@Override
			public void process(Exchange exchange) throws Exception {
				EbayItem item = exchange.getIn().getBody(EbayItem.class);
				exchange.getIn().setHeader("imageURL", item.getGaleryURL());							
			}
		})
        .to(header("imageURL").toString())
        .to("file:///tmp/?fileName=yourFileName.xml");

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
