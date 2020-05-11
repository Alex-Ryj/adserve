package com.arit.adserve.providers.ebay;

import com.arit.adserve.verticle.EbayApiVerticle;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing Camel route related to eBay processing.
 * Route nodes responsible for 'external' calls are dynamically replaced by mocks
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:app.properties")
@SpringBootTest
public class EBayCamelTest {
    
    @Autowired
    protected CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;
    
    @Autowired
    private EbayApiVerticle ebayApi;
 

    @Test
    public void vertxRouteTest() throws Exception {

        camelContext.getRouteDefinition(EbayApiVerticle.ROUTE_VTX_EBAY_REQ_BRIDGE)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:in");
                        // weaveById() replace camel step id identified by id()
                        weaveById("id-vertx-ebay-req-bridge-end").replace().log("replacing call to another route");
                        // send the outgoing message to mock:out
                        weaveAddLast().to("mock:out");
                    }
                });

        camelContext.start();
        MockEndpoint mockOut = camelContext.getEndpoint("mock:out", MockEndpoint.class);
        mockOut.expectedMessageCount(1);
        template.sendBody("direct:in","test");
        mockOut.assertIsSatisfied();
    }

    @Test
    public void vertxGetImageTest() throws Exception {

        camelContext.getRouteDefinition(EbayApiVerticle.ROUTE_GET_FILE_HTTP)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:in1");
                        // send the outgoing message to mock:out
                        weaveAddLast().to("mock:out1");
                    }
                });

        camelContext.start();
        MockEndpoint mockOut = camelContext.getEndpoint("mock:out1", MockEndpoint.class);
        mockOut.expectedMessageCount(1);
        template.sendBody("direct:in1","test");
        mockOut.assertIsSatisfied();

    }
}
