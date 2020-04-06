package com.arit.adserve.ebay;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@EnableAutoConfiguration
public class EBayCamelTest {

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(context = "direct:remoteEbayApi")
    protected MockEndpoint foo;


    @Test
    @DirtiesContext
    public void testMocksAreValid() throws Exception {


        foo.message(0).header("bar").isEqualTo("ABC");

        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
