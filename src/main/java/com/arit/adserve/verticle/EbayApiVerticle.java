package com.arit.adserve.verticle;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.providers.ApiUtils;
import com.arit.adserve.providers.ebay.EBayRequestService;
import com.arit.adserve.providers.ebay.RequestState;
import com.arit.adserve.rules.Evaluate;

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
    
    public static boolean readyToProcess() {
        boolean readyToProcess = EbayApiVerticle.readyToProcess.get();
        if (readyToProcess) {
        	EbayApiVerticle.readyToProcess.set(false);
        }
        return readyToProcess;
    }

    private RouteBuilder configureRoutes() {
        return new RouteBuilder() {
            public void configure() throws Exception {
            	
            	String requestState = "requestState";            	
            	
                from("vertx:" + VTX_EBAY_REQUEST)
                        .routeId(ROUTE_VTX_EBAY_REQ_BRIDGE)
                        .to("direct:getItems")
                        .id("id_vertx_ebay_req_bridge_end");
                
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
						.to("direct:remoteEbayApiGetItems").otherwise()
						.log(LoggingLevel.INFO, "no item processing this time");

                from("direct:remoteEbayApiGetItems")
                        .routeId(ROUTE_GET_EBAY_ITEMS)
                        .removeHeaders("CamelHttp*")
                        .toD(eBayRequestService.getFindRequestUrl())
                        .id("id_ebay_http_call")
                        .process(exchange -> {
                        	String jsonResp = (String) exchange.getIn().getBody();
                        	JsonObject jsonObj = new JsonObject(jsonResp).getJsonObject("findItemsByKeywordsResponse")
                    				.getJsonObject("paginationOutput");		
                    		long pagesTotal = Long.parseLong(jsonObj.getString("totalPages"));
                    		long itemsTotalInRequest = Long.parseLong(jsonObj.getString("totalEntries"));
                    		long pageNumber = Long.parseLong(jsonObj.getString("pageNumber"));
                    		long itemsPerPage = Long.parseLong(jsonObj.getString("entriesPerPage"));  
                    		eBayRequestService.updateEbayFindRequest(pageNumber, itemsPerPage, itemsTotalInRequest, pagesTotal);
                        })
                        .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item")
                        .bean(convert)
                        .bean(evaluate)
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            log.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());
                            itemService.save(item);
                        })
                        .process(exchange -> readyToProcess.set(true))
                        .to("direct:getImage");
                
                from("direct:remoteEbayApiUpdateItems")
                .process(exchange -> readyToProcess.set(true))
                .log("log:updateItems");

                from("direct:getImage")
                .process(exchange -> {
                	Item item = exchange.getIn().getBody(Item.class);
                	exchange.getIn().setHeader("hasImage", itemService.hasImage(new ItemId(item.getProviderItemId(), Constants.EBAY)));
                	
                })
 		       .filter().simple("${header.hasImage} == false")
                		.routeId(ROUTE_GET_EBAY_IMAGE)
                        .setHeader("Accept", simple("image/jpeg"))
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            exchange.getIn().setBody(item.getGaleryURL().replace("https", "https4"));
                            log.info("imageURL: {}", item.getGaleryURL());
                            exchange.getIn().setHeader("imageFile", "ebay-" + item.getProviderItemId());
                            exchange.getIn().setHeader("providerItemId", item.getProviderItemId());
                            exchange.getIn().setHeader("providerName", item.getProviderName());
                        })
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .toD("${body}")
                        .marshal().base64()
                        .process(exchange -> {
                            String imageStr = exchange.getIn().getBody(String.class);
                            log.debug(imageStr);
                            Optional<Item> optItem = Optional.ofNullable( itemService.findById(new ItemId(
                            		exchange.getIn().getHeader("providerItemId").toString(),
                            		exchange.getIn().getHeader("providername").toString())));
                            if (optItem.isPresent()) {
                                Item item = optItem.get();
                                item.setImage64BaseStr(imageStr);
                                log.debug("saving {}", item);
                                itemService.save(item);
                            }
                        })
                        .unmarshal().base64()
                        .toD("file:///tmp/ebay/?fileName=${header.imageFile}.jpg")
                        .to("log:image");

              from("vertx:" + VTX_EBAY_GET_IMAGE_CAMEL)
                        .routeId(ROUTE_GET_FILE_HTTP)
                      .process(exchange -> {
                          File file = new File("C://Temp/img.png");
                          String fileStr = Base64.getEncoder().encodeToString(IOUtils.toByteArray(new FileInputStream(file)));
                          exchange.getIn().setBody(fileStr);
                          exchange.getIn().removeHeaders("*");
                          exchange.getOut().setBody(new JsonObject().put("img", fileStr));
                      })
              .log("${body}");
            }
        };
    }
}
