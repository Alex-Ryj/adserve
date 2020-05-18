package com.arit.adserve.verticle;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.providers.ebay.EBayRequestService;
import com.arit.adserve.providers.ebay.RequestState;
import com.arit.adserve.rules.Evaluate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * a service to deal with eBay items: get, update, delete
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@Slf4j
@Service
public class EbayApiVerticle extends AbstractVerticle {

    /**
     * a request from vert.x to a camel route
     */
    public static final String VTX_EBAY_REQUEST = "ebayReq";
    /**
     * a request from vert.x to a get an image from camel
     */
    public static final String VTX_EBAY_GET_IMAGE_CAMEL = "ebayResImgCamel";
    /**
     * vert.x to camel route id
     */
    public static final String ROUTE_VTX_EBAY_REQ_BRIDGE = "route_vertx_ebay_req_bridge";
    /**
     * get eBay items via finding API
     */
    public static final String ROUTE_GET_EBAY_ITEMS = "route_get_ebay_items";
    /**
     * initial route for choosing if items need to be retrieved or updated
     */
    public static final String ROUTE_PROCESS_EBAY_ITEMS = "route_process_ebay_items";
    /**
     * get item image
     */
    public static final String ROUTE_GET_EBAY_IMAGE = "route_get_ebay_image";
    /**
     * render file content
     */
    public static final String ROUTE_GET_FILE_HTTP = "route_get_file_http";    
    
    private static final int maxItemsForUpdate = 20;
    
    private static final AtomicBoolean readyToProcess = new AtomicBoolean(true);
    
    @Autowired
    private CamelContext camelContext;
    @Autowired
    private ItemJsonConvert convert;
    @Autowired
    private Evaluate evaluate;
    @Autowired
    private ItemService itemService;
    @Autowired
    private EBayRequestService eBayRequestService;

    @Override
    public void start() throws Exception {
        super.start();
//        vertx.eventBus().consumer(EBAY_REQUEST_VTX, message -> {
//            System.out.println("ANNOUNCE >> " + message.body());
//            message.reply("ok from ebay");
//        });
        camelContext.addRoutes(configureRoutes());
    }
    
    /**
     * it sets an atomic flag to allow next processing to eBay request
     * @return boolean flag
     */
    public static boolean readyToProcess() {
        boolean readyToProcess = EbayApiVerticle.readyToProcess.get();
        if (readyToProcess) {
        	EbayApiVerticle.readyToProcess.set(false);
        }
        return readyToProcess;
    }

    private RouteBuilder configureRoutes() {
        return new RouteBuilder() {
            @SuppressWarnings("unchecked")
			public void configure() throws Exception {
            	
            	String requestState = "requestState";   
            
            	
            	//root to initiate of eBay items processing via vert.x message
                from("vertx:" + VTX_EBAY_REQUEST)
                        .routeId(ROUTE_VTX_EBAY_REQ_BRIDGE)
                        .to("direct:processItems")
                        .id("id_vertx_ebay_req_bridge_end");
                
                //root to select what eBay items processing is required base don the item status
                from("direct:processItems")
                .routeId(ROUTE_PROCESS_EBAY_ITEMS)
                .filter(method(EbayApiVerticle.class, "readyToProcess"))
                .throttle(1).timePeriodMillis(10000)  //allow only one message every 10 sec
                .process(exchange -> 
                	exchange.getIn().setHeader(requestState, eBayRequestService.updateRequestState().toString())                )
						.choice().when(header(requestState).isEqualTo(RequestState.RETRIEVE_ITEMS.toString()))
						.to("direct:remoteEbayApiGetItems")
						.when(header(requestState).isEqualTo(RequestState.UPDATE_ITEMS.toString()))
						.to("direct:remoteEbayApiUpdateItems")
						.when(header(requestState).isEqualTo(RequestState.CHANGE_SEARCH.toString()))
						.process(exchange -> {
							eBayRequestService.setNextKeyWords();
							eBayRequestService.updateRequestState(); //this should update the state to RETRIEVE_ITEMS
						})
						.to("direct:remoteEbayApiGetItems")
						.otherwise()
						.log(LoggingLevel.INFO, "no item processing this time");

                //get ebay items processing
                from("direct:remoteEbayApiGetItems")
                        .routeId(ROUTE_GET_EBAY_ITEMS)
                        .process(exchange -> {
                        	String requestQuery = eBayRequestService.getFindRequestQuery();
                        	log.info("requestQuery: {}", requestQuery);
                        	exchange.getIn().setHeader("requestQuery", requestQuery);
                        })
                        .log(LoggingLevel.INFO, "header.requestQuery + ${header.requestQuery}")
                        .removeHeaders("CamelHttp*") 
//                        .setHeader(Exchange.HTTP_PATH, expression("/login"))
                        .toD("https:svcs.ebay.com/services/search/FindingService/v1?${header.requestQuery}") //simple("${header.requestUrl}").getText())                        
                        .id("id_ebay_http_call")
                        .process(exchange -> {
                        	String jsonResp = exchange.getIn().copy().getBody(String.class);
                        	log.info(jsonResp);
                        	JsonNode jsonObj = new ObjectMapper().readTree(jsonResp).get("findItemsByKeywordsResponse").get(0).get("paginationOutput").get(0);	
                    		long pagesTotal = Long.parseLong(jsonObj.get("totalPages").get(0).asText());
                    		long itemsTotalInRequest = Long.parseLong(jsonObj.get("totalEntries").get(0).asText());
                    		long pageNumber = Long.parseLong(jsonObj.get("pageNumber").get(0).asText());
                    		long itemsPerPage = Long.parseLong(jsonObj.get("entriesPerPage").get(0).asText());  
                    		eBayRequestService.updateEbayFindRequest(pageNumber, itemsPerPage, itemsTotalInRequest, pagesTotal);
                        })
                        .process(exchange -> readyToProcess.set(true))
                        .log(LoggingLevel.DEBUG, "${body}")
                        .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item[*]")
                        .bean(convert, "getEbayItem")
                        .bean(evaluate)
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            log.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());
                            if(item.isProcess()) itemService.save(item);
                        })
                        .filter(simple("${mandatoryBodyAs(com.arit.adserve.entity.Item).isProcess()}"))
                        .to("direct:getImage");
                
                //update existing items from eBay
                from("direct:remoteEbayApiUpdateItems")
                .process(exchange -> {
                	List<Item> items = itemService.getItemsFromProviderBefore(eBayRequestService.getDateLimitForItems(), Constants.EBAY, maxItemsForUpdate);
                	List<String> eBayItemIds = items.stream().map(Item::getProviderItemId).collect(Collectors.toList());
                	exchange.getIn().setHeader("itemsUpdateUrl", eBayRequestService.getFindItemsUrl(eBayItemIds));
                })
                .removeHeaders("CamelHttp*")
                .toD(simple("${header.itemsUpdateUrl}").getText())
                // step to delete items that are no longer on eBay
                .process(exchange -> {
                	String jsonResp = (String) exchange.getIn().getBody();
                	List<String> itemIds = (List<String>) exchange.getIn().getHeader("itemIds");
                	for (String itemId : itemIds) {
						if(!jsonResp.contains(itemId)) {
							Item item = itemService.findById(new ItemId(itemId, Constants.EBAY));
							item.setDeleted(true);
							itemService.update(item);
						}
					}
                	
                })
                .process(exchange -> readyToProcess.set(true))
                .log("log:updateItems");

                //get item image from eBay
                from("direct:getImage")
                .routeId(ROUTE_GET_EBAY_IMAGE)
                .process(exchange -> {
                	Item item = exchange.getIn().getBody(Item.class);
                	exchange.getIn().setHeader("hasImage", itemService.hasImage(new ItemId(item.getProviderItemId(), Constants.EBAY)));
                	
                })
 		       .filter().simple("${header.hasImage} == false")                		
                        .setHeader("Accept", simple("image/jpeg"))
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            exchange.getIn().setBody(item.getGaleryURL().replace("https", "https4"));
                            log.info("imageURL: {}", item.getGaleryURL());
                            exchange.getIn().setHeader("providerItemId", item.getProviderItemId());
                            exchange.getIn().setHeader("providerName", item.getProviderName());
                        })
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .toD("${body}")
                        .marshal().base64()
                        .process(exchange -> {
                            String imageStr = exchange.getIn().getBody(String.class);                          
                            Optional<Item> optItem = Optional.ofNullable( itemService.findById(new ItemId(
                            		exchange.getIn().getHeader("providerItemId").toString(),
                            		exchange.getIn().getHeader("providerName").toString())));
                            if (optItem.isPresent()) {
                                Item item = optItem.get();
                                item.setImage64BaseStr(imageStr);
                                log.debug("saving {}", item);
                                itemService.save(item);
                            }
                        })                        
						.log(LoggingLevel.INFO,
								MessageFormat.format("saved image for item id: {1}", simple("${header.providerItemId").getText()));

                //test root //TODO: remove this
				from("vertx:" + VTX_EBAY_GET_IMAGE_CAMEL).routeId(ROUTE_GET_FILE_HTTP).process(exchange -> {
					//File file = new File("C://Temp/img.png");
					String fileStr = "img base64 str"; // Base64.getEncoder().encodeToString(IOUtils.toByteArray(new FileInputStream(file)));
					exchange.getIn().setBody(fileStr);
					exchange.getIn().removeHeaders("*");
					exchange.getOut().setBody(new JsonObject().put("img", fileStr));
				}).log("${body}");
            }
        };
    }
}
