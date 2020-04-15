package com.arit.adserve.ebay;

import com.arit.adserve.entity.repository.ItemRepository;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:app.properties")
@SpringBootTest
public class EBayCamelTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Before
    public void setUp() throws Exception {
        EbayApi ebayApi = applicationContext.getBean(EbayApi.class);
//        ebayApi.start();
    }

    @Test
    public void vertxRouteTest() throws Exception {

        camelContext.getRouteDefinition("route-vertx-ebay-req-bridge")
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

        camelContext.getRouteDefinition("route-get-file-http")
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:in1");
                        // send the outgoing message to mock:out
                        weaveAddLast().to("mock:out1");
                    }
                });

        camelContext.start();
        MockEndpoint mockOut = camelContext.getEndpoint("mock:out", MockEndpoint.class);
        mockOut.expectedMessageCount(1);
        template.sendBody("direct:in1","test");
        mockOut.assertIsSatisfied();

    }
}
