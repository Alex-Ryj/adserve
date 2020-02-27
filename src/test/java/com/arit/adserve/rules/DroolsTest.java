package com.arit.adserve.rules;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import com.arit.adserve.entity.Item;
import com.arit.adserve.providers.ebay.EBayFindRequest;
import com.arit.adserve.providers.ebay.RequestState;

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
        
        EBayFindRequest fr = new EBayFindRequest();
        fr.setState(RequestState.INITIAL);
        kSession.insert(fr);
        kSession.fireAllRules(); 
        assertTrue(item.isProcess());
    }
    
    @Test
	public void testEBayREquest() throws Exception {
    	  EBayFindRequest fr = new EBayFindRequest();
          fr.setState(RequestState.INITIAL);
          kSession.insert(fr);
          kSession.fireAllRules(); 
          assertEquals(RequestState.INITIAL, fr.getState());
	}

}
