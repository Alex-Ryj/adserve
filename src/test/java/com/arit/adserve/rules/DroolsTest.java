package com.arit.adserve.rules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import com.arit.adserve.entity.Item;

public class DroolsTest {

	private KieSession kSession;
	 
    @Before
    public void setup() throws IOException {        
        kSession = new DroolsConfig().getKieSession();
    }
 
    @Test
    public void testEbayItem(){
        Item item = new Item();
        item.setPrice(7000);
        item.setCondition("Used");
        kSession.insert(item); 
        kSession.fireAllRules(); 
        assertTrue(item.isProcess());
    }

}
