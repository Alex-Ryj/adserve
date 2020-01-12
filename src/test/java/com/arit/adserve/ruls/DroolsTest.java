package com.arit.adserve.ruls;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import com.arit.adserve.ebay.EbayItem;
import com.arit.adserve.rules.DroolsConfig;

public class DroolsTest {

	private KieSession kSession;
	 
    @Before
    public void setup() throws IOException {        
        kSession = new DroolsConfig().getKieSession();
    }
 
    @Test
    public void testEbayItem(){
        EbayItem item = new EbayItem("", "", "", 5, "used");
        kSession.insert(item); 
        kSession.fireAllRules(); 
        assertTrue(item.isProcess());
    }

}
