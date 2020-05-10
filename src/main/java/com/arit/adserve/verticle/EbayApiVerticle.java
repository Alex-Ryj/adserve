package com.arit.adserve.verticle;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.providers.IApiCall;
import com.arit.adserve.providers.ebay.EBayRequestService;
import com.arit.adserve.rules.Evaluate;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EbayApiVerticle extends AbstractVerticle implements IApiCall {

    public static final String VTX_EBAY_REQUEST = "ebayReq";
    public static final String VTX_EBAY_GET_IMAGE_CAMEL = "ebayResImgCamel";
    public static final String route_vertx_ebay_req_bridge = "route_vertx_ebay_req_bridge";
    @Autowired
    private CamelContext camelContext;
    @Autowired
    private ItemJsonConvert convert;
    @Autowired
    private Evaluate evaluate;
    @Autowired
    private ItemService itemService;
    @Autowired
    private EBayRequestService eBayFindRequestService;

    @Override
    public void start() throws Exception {
        super.start();
//        vertx.eventBus().consumer(EBAY_REQUEST_VTX, message -> {
//            System.out.println("ANNOUNCE >> " + message.body());
//            message.reply("ok from ebay");
//        });
        camelContext.addRoutes(configureRoutes());
        camelContext.start();
    }

    private RouteBuilder configureRoutes() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("vertx:" + VTX_EBAY_REQUEST)
                        .routeId("route_vertx_ebay_req_bridge")
                        .to("direct:getItems")
                        .id("id_vertx_ebay_req_bridge_end");

                from("direct:remoteEbayApi")
                        .routeId("route_get_ebay_items")
                        .removeHeaders("CamelHttp*")
                        .to(eBayFindRequestService.getFindRequestUrl())
                        .process(exchange -> {
                        	String jsonResp = (String) exchange.getIn().getBody();
                        	

                        })
                        .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item")
                        .bean(convert)
                        .bean(evaluate)
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            log.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());
                            itemService.save(item);
                        })
                        .to("direct:getImage");

                from("direct:getImage")
//		.filter().simple("${body.process} == true")
                		.routeId("route_get_ebay_image")
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
                        .routeId("route_get_file_http")
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
