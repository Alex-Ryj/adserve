package com.arit.adserve.providers.ebay;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.arit.adserve.verticle.EbayApiVerticle;

/**
 * Testing Camel route related to eBay processing. Route nodes responsible for
 * 'external' calls can be dynamically replaced by mocks
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
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
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayApiVerticle.ROUTE_VTX_EBAY_REQ_BRIDGE, a -> {
			a.replaceFromWith("direct:in");
			// weaveById() replace camel step id identified by id()
			a.weaveById("id_vertx_ebay_req_bridge_end").replace().log("replacing call to another route");
			// send the outgoing message to mock:out
			a.weaveAddLast().to("mock:out");
		});
		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out", MockEndpoint.class);
		mockOut.expectedMessageCount(1);
		template.sendBody("direct:in", "test");
		mockOut.assertIsSatisfied();
	}

	@Test
	public void vertxGetImageTest() throws Exception {
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayApiVerticle.ROUTE_GET_FILE_HTTP, a -> {
			a.replaceFromWith("direct:in1");
			// send the outgoing message to mock:out1
			a.weaveAddLast().to("mock:out1");
		});

		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out1", MockEndpoint.class);
		mockOut.expectedMessageCount(1);
		template.sendBody("direct:in1", "test");
		mockOut.assertIsSatisfied();
	}

	@Test
	public void testEbayApiGetItems() throws Exception {
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayApiVerticle.ROUTE_PROCESS_EBAY_ITEMS, a -> {
			a.weaveAddLast().to("mock:out2");
		});
		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out2", MockEndpoint.class);
		mockOut.expectedMessageCount(1);
		template.sendBody("direct:processItems", "test");
		template.sendBody("direct:processItems", "test");
		mockOut.assertIsSatisfied();
	}
}
