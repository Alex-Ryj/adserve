package com.arit.adserve.ebay;

import com.arit.adserve.comm.IApiCall;
import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;
import com.arit.adserve.rules.Evaluate;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EbayApi extends RouteBuilder implements IApiCall {

    private static Logger logger = LoggerFactory.getLogger(EbayApi.class);

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
    private ItemJsonConvert convert;

    @Autowired
    private Evaluate evaluate;

    @Autowired
    private ItemRepository itemRepository;


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

        rest("/test/")
                .id("test-route")
                .get("db")
                .to("direct:getItems");

        from("direct:getItems")
                .process(exchange -> {
                    Iterable<Item> items = itemRepository.findAll();
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
                    logger.info("{} - {} - {} - {}", item.isProcess(), item.getCondition(), item.getPrice(), item.getTitle());
                    itemRepository.save(item);
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
                    logger.info("imageURL: {}", item.getGaleryURL());
                    exchange.getIn().setHeader("imageFile", "ebay-" + item.getItemId());
                    exchange.getIn().setHeader("itemId", item.getItemId());
                })
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("${body}")
                .marshal().base64()
                .process(exchange -> {
                    String imageStr = exchange.getIn().getBody(String.class);
                    logger.info(imageStr);
                    Optional<Item> optItem = itemRepository.findById(exchange.getIn().getHeader("itemId").toString());
                    if (optItem.isPresent()) {
                        Item item = optItem.get();
                        item.setImage64BaseStr(imageStr);
                        logger.info("saving {}", item);
                        itemRepository.save(item);
                    }
                })
                .unmarshal().base64()
                .toD("file:///tmp/ebay/?fileName=${header.imageFile}.jpg")
                .to("log:image");

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
