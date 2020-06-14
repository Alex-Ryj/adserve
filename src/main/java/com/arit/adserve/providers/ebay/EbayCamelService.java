package com.arit.adserve.providers.ebay;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.mongo.ItemMongo;
import com.arit.adserve.entity.mongo.service.ItemMongoService;
import com.arit.adserve.rules.Evaluate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class EbayCamelService {

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
     * update items
     */
    public static final String ROUTE_UPDATE_EBAY_ITEMS = "route_update_ebay_items";
    /**
     * get item image
     */
    public static final String ROUTE_GET_EBAY_IMAGE = "route_get_ebay_image";
    /**
     * render file content
     */
    public static final String ROUTE_GET_FILE_HTTP = "route_get_file_http";    
    
    private static final int MAX_ITEMS_FOR_UPDATE = 20;
    
    private static final AtomicBoolean readyToProcess = new AtomicBoolean(true);
    
 
    @Autowired
    private ItemJsonConvert convert;
    @Autowired
    private Evaluate evaluate;
    @Autowired
    private ItemMongoService itemService;
    @Autowired
    private EBayRequestService eBayRequestService;    
    @Qualifier("transactionReadUncommitted")
    @Autowired
    private TransactionTemplate transactionTemplate;  
    
    /**
     * it sets an atomic flag to allow next processing to eBay request
     * @return boolean flag
     */
    public static boolean readyToProcess() {
        boolean readyToProcess = EbayCamelService.readyToProcess.get();
        if (readyToProcess) {
        	EbayCamelService.readyToProcess.set(false);
        }
        return readyToProcess;
    }

    public RouteBuilder configureRoutes() {
        return new RouteBuilder() {
            @SuppressWarnings("unchecked")
			public void configure() throws Exception {
            	
            	String requestState = "requestState";
            	
//------------- Route to initiate of eBay items processing via vert.x message
                from("vertx:" + VTX_EBAY_REQUEST)
                        .routeId(ROUTE_VTX_EBAY_REQ_BRIDGE)
                        .to("direct:processItems")
                        .id("id_vertx_ebay_req_bridge_end"); 
                
//------------- Route to select what eBay items processing is required based on the item status
                from("direct:processItems")
                .routeId(ROUTE_PROCESS_EBAY_ITEMS)
                .filter(method(EbayCamelService.class, "readyToProcess"))
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

//------------- Route to get eBay items processing
                from("direct:remoteEbayApiGetItems")
                        .routeId(ROUTE_GET_EBAY_ITEMS)
                        .process(exchange -> {
                        	String requestQuery = eBayRequestService.getFindRequestQuery();
                        	exchange.getIn().setHeader("requestQuery", requestQuery);
                        })
                        .log(LoggingLevel.INFO, "header.requestQuery: ${header.requestQuery}")
                        .removeHeaders("CamelHttp*") 
                        .toD("https:svcs.ebay.com/services/search/FindingService/v1?${header.requestQuery}") //simple("${header.requestUrl}").getText())                        
                        .id("id_ebay_http_call")
                        .process(exchange -> {
                        	String jsonResp = exchange.getIn().getBody(String.class);
                        	JsonNode jsonObj = new ObjectMapper().readTree(jsonResp).get("findItemsByKeywordsResponse").get(0).get("paginationOutput").get(0);	
                    		long pagesTotal = Long.parseLong(jsonObj.get("totalPages").get(0).asText());
                    		long itemsTotalInRequest = Long.parseLong(jsonObj.get("totalEntries").get(0).asText());
                    		long pageNumber = Long.parseLong(jsonObj.get("pageNumber").get(0).asText());
                    		long itemsPerPage = Long.parseLong(jsonObj.get("entriesPerPage").get(0).asText());  
                    		log.info("updateing request pageNum {}, itemsPerPage {}, itemsTotalInRequest {}, pagesTotal {}", pageNumber, itemsPerPage, itemsTotalInRequest, pagesTotal);
                    		eBayRequestService.updateEbayFindRequest(pageNumber, itemsPerPage, itemsTotalInRequest, pagesTotal);
                    		exchange.getIn().setBody(new ObjectMapper().readTree(jsonResp));  //TODO: preserve original exchange as JSON object in camel 3.3
                        })
                        .process(exchange -> readyToProcess.set(true))
                        .log(LoggingLevel.DEBUG, "${body}")
                        .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item[*]")
                        .bean(convert, "getEbayItemMongo")
                        .bean(evaluate)
                        .process(exchange -> {
                            ItemMongo item = exchange.getIn().getBody(ItemMongo.class);							
							 ItemMongo existingItem =
							  itemService.findByProviderId(item.getProviderItemId(), Constants.EBAY);
							 if(existingItem != null) { 
						      item.setProcess(false);
						      existingItem.setId(existingItem.getId()); //update item by 
						      existingItem.setUpdatedOn(new Date()); 
							  itemService.save(existingItem); }
							 
                            log.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());                            
                            if(item.isProcess()) itemService.save(item);
                        })
                        .filter(simple("${mandatoryBodyAs(com.arit.adserve.entity.mongo.ItemMongo).isProcess()}"))
                        .to("direct:getImage");
               
//------------- Route to update existing items from eBay
                from("direct:remoteEbayApiUpdateItems")
                .routeId(ROUTE_UPDATE_EBAY_ITEMS)
                .process(exchange -> {
                	List<ItemMongo> items = itemService.getItemsFromProviderBefore(eBayRequestService.getDateLimitForItems(), Constants.EBAY, MAX_ITEMS_FOR_UPDATE);
                	List<String> eBayItemIds = items.stream().map(ItemMongo::getProviderItemId).collect(Collectors.toList());
                	exchange.getIn().setHeader("itemIds", eBayItemIds);
                	String itemsUpdateUrl = eBayRequestService.getFindItemsUrl(eBayItemIds);
                	if(itemsUpdateUrl != null) exchange.getIn().setHeader("itemsUpdateUrl", eBayRequestService.getFindItemsUrl(eBayItemIds));
                })
                .filter(simple("${header.itemsUpdateUrl} != null"))
                .removeHeaders("CamelHttp*")
                .toD(simple("${header.itemsUpdateUrl}").getText())
                // step to delete items that are no longer on eBay
                .process(exchange -> {
                	String jsonResp = exchange.getIn().getBody(String.class);                	
                	JsonNode jsonArrObj = new ObjectMapper().readTree(jsonResp).get("Item");
                	List<String> itemEbayIds = (List<String>) exchange.getIn().getHeader("itemIds");

							transactionTemplate.execute(new TransactionCallback() {
								@Override
								public Object doInTransaction(TransactionStatus status) {
									Iterable<ItemMongo> items = itemService.findAllByProviderIds(itemEbayIds, Constants.EBAY);
									try {
										Iterable<ItemMongo> updatedItemd = convert.updateEbayItemsMongo(jsonResp, items);
										itemService.updateAll(updatedItemd);
									} catch (IOException e) {
										log.error("updating items", e);
									}									
									for (ItemMongo item : items) {
										boolean delete = true;
										for (JsonNode jsonObj : jsonArrObj) {
											if (item.getProviderItemId().equals(jsonObj.get("ItemId").toString()))
												delete = false;
										}
										item.setDeleted(delete);
										itemService.save(item);
									}
									return null;
								}
							}
						);
						})
                .process(exchange -> readyToProcess.set(true))
                .log("log:updateItems");

//------------- Route to get an item image from eBay
                from("direct:getImage")
                .routeId(ROUTE_GET_EBAY_IMAGE)
                .process(exchange -> {
                	ItemMongo item = exchange.getIn().getBody(ItemMongo.class);
                	exchange.getIn().setHeader("hasImage", itemService.hasImage(item.getProviderItemId(), Constants.EBAY));
                	
                })
 		       .filter().simple("${header.hasImage} == false")                		
                        .setHeader("Accept", simple("image/jpeg"))
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .process(exchange -> {
                            ItemMongo item = exchange.getIn().getBody(ItemMongo.class);
                            exchange.getIn().setBody(item.getGalleryURL());
                            log.info("imageURL: {}", item.getGalleryURL());
                            exchange.getIn().setHeader("hasGalleryURL", item.getGalleryURL() != null);
                            exchange.getIn().setHeader("providerItemId", item.getProviderItemId());
                            exchange.getIn().setHeader("providerName", item.getProviderName());
                        })
                        .filter().simple("${header.hasGalleryURL} == true") 
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .toD("${body}")
                        .marshal().base64()
						.process(exchange -> {
							String imageStr = exchange.getIn().getBody(String.class);
							transactionTemplate.execute(new TransactionCallback() {
								public Object doInTransaction(TransactionStatus status) {
									Optional<ItemMongo> optItem = Optional.ofNullable(itemService.findByProviderId(
											exchange.getIn().getHeader("providerItemId").toString(),
													exchange.getIn().getHeader("providerName").toString()));
									if (optItem.isPresent()) {
										ItemMongo item = optItem.get();										
										item.setImage64BaseStr(imageStr);
										log.debug("saving {}", item);
										itemService.save(item);
									}
									return null;
								}								
                            }
                        );     
                        })  
						.log(LoggingLevel.INFO,
								MessageFormat.format("saved image for item id: {0}", simple("${header.providerItemId}").getText()));

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
