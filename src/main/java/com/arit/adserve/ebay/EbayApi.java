package com.arit.adserve.ebay;

import com.arit.adserve.comm.IApiCall;
import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.service.ItemService;
import com.arit.adserve.rules.Evaluate;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EbayApi extends AbstractVerticle implements IApiCall {

    private static Logger log = LoggerFactory.getLogger(EbayApi.class);

    private Map<String, String> endpoints = new HashMap<>();

    public static final String EBAY_REQUEST_VTX = "ebayReq";

    public static final String EBAY_GET_IMAGE_CAMEL = "ebayResImgCamel";

    @Autowired
    private CamelContext camelContext;

    @Value("${EBAY_APP_ID}")
    private String ebayAppId;

    @Value("${EBAY_GLOBAL_ID}")
    private String ebayGlobalId;

    @Value("${EBAY_SITE_ID}")
    private String ebaySiteId;

    @Autowired
    private ItemJsonConvert convert;

    @Autowired
    private Evaluate evaluate;

    @Autowired
    private ItemService itemService;

    public EbayApi() {
        endpoints.put("Finding", "https4://svcs.ebay.com/services/search/FindingService/v1?");
        endpoints.put("Shopping", "http4://open.api.ebay.com/shopping?");
        endpoints.put("SOAP", "https4://api.ebay.com/wsapi");
    }

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

                from("direct:getItems")
                        .id("get-items-from-db")
                        .process(exchange -> {
                            Iterable<Item> items = itemService.findAll(0, 10);
                            exchange.getIn().setBody(items);
                        });

                from("direct:remoteEbayApi")
                        .id("get-items-route")
                        .removeHeaders("CamelHttp*")
                        .to(endpoints.get("Finding") + getParams())
                        .split().jsonpathWriteAsString("$.findItemsByKeywordsResponse[0].searchResult[0].item")
                        .bean(convert)
                        .bean(evaluate)
                        .process(exchange -> {
                            Item item = exchange.getIn().getBody(Item.class);
                            log.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());
                            itemService.save(item);
                        })
                        .to("log:item")
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
                            log.info(imageStr);
                            Optional<Item> optItem = Optional.ofNullable(itemService.findById(exchange.getIn().getHeader("itemId").toString()));
                            if (optItem.isPresent()) {
                                Item item = optItem.get();
                                item.setImage64BaseStr(imageStr);
                                EbayApi.log.info("saving {}", item);
                                itemService.save(item);
                            }
                        })
                        .unmarshal().base64()
                        .toD("file:///tmp/ebay/?fileName=${header.imageFile}.jpg")
                        .to("log:image");

              from("vertx:" + EBAY_GET_IMAGE_CAMEL)
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
