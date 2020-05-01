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
import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.providers.IApiCall;
import com.arit.adserve.providers.ebay.EBayFindRequestService;
import com.arit.adserve.rules.Evaluate;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EbayApiVerticle extends AbstractVerticle implements IApiCall {


    public static final String EBAY_REQUEST_VTX = "ebayReq";

    public static final String EBAY_GET_IMAGE_CAMEL_VTX = "ebayResImgCamel";

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ItemJsonConvert convert;

    @Autowired
    private Evaluate evaluate;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EBayFindRequestService eBayFindRequestService;



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
                from("vertx:" + EBAY_REQUEST_VTX)
                        .routeId("route-vertx-ebay-req-bridge")
                        .to("direct:getItems")
                        .id("id-vertx-ebay-req-bridge-end");

                from("direct:remoteEbayApi")
                        .id("get-items-route")
                        .removeHeaders("CamelHttp*")
                        .to(eBayFindRequestService.getRequestUrl())
                        .process(exchange -> {

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
                        .setHeader("Accept", simple("image/jpeg"))
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            exchange.getIn().setBody(item.getGaleryURL().replace("https", "https4"));
                            log.info("imageURL: {}", item.getGaleryURL());
                            exchange.getIn().setHeader("imageFile", "ebay-" + item.getItemId());
                            exchange.getIn().setHeader("itemId", item.getItemId());
                        })
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .toD("${body}")
                        .marshal().base64()
                        .process(exchange -> {
                            String imageStr = exchange.getIn().getBody(String.class);
                            log.debug(imageStr);
                            Optional<Item> optItem = Optional.ofNullable(itemService.findById(exchange.getIn().getHeader("itemId").toString()));
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

              from("vertx:" + EBAY_GET_IMAGE_CAMEL_VTX)
                        .routeId("route-get-file-http")
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
