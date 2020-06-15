package com.arit.adserve.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.arit.adserve.providers.ebay.EBayFindRequest;
import com.arit.adserve.providers.ebay.EBayRequestService;
import com.arit.adserve.providers.ebay.RequestState;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class EBayRequestDroolsTest {

	private static KieSession kSession;
	private static StatelessKieSession kStatelessSession;
	
    @Autowired
    private Evaluate evaluate;
	 
    @BeforeAll
    public static void setup() throws IOException {        
        kSession = new DroolsConfig().getKieSession();
        kStatelessSession = new DroolsConfig().getKieStatlessSession();
    } 
    
    
    @Test
	public void testEBayRequestRetieveMoreItems() throws Exception {
    	 kSession = new DroolsConfig().getKieSession();
    	  EBayFindRequest fr = new EBayFindRequest();
      	  kSession.getAgenda().getAgendaGroup(EBayRequestService.RULES_EBAY_REQUEST_AGENDA).setFocus(); //focus on ebay request rules
          // initial state
    	  fr.setItemsTotal(0);
    	  fr.setItemsMaxRequired(200);
    	  fr.setSearchWords("searchWords_0");    	  
    	  fr.setState(RequestState.RETRIEVE_ITEMS);
    	  fr.setPageNumber(2);
    	  fr.setItemsTotalInRequest(100000);
    	  fr.setPagesTotal(1000);
    	  fr.setItemsPerPage(100);
          kSession.insert(fr);         
          kSession.fireAllRules(); 
          assertEquals(RequestState.RETRIEVE_ITEMS, fr.getState());
          assertEquals(2, fr.getPageNumber());
          log.info("fact count {}", kSession.getFactCount());
          kSession.dispose();
          // after processing the first response
          fr.setItemsTotalInRequest(200);
          fr.setItemsPerPage(100);
          fr.setPagesTotal(2);
          fr.setItemsTotal(100);
          kSession = new DroolsConfig().getKieSession();
          kSession.getAgenda().getAgendaGroup(EBayRequestService.RULES_EBAY_REQUEST_AGENDA).setFocus();
          kSession.insert(fr);
          log.info("fact count {}", kSession.getFactCount());
          kSession.fireAllRules(); 
          log.info("fact count {}", kSession.getFactCount());
          assertEquals(RequestState.RETRIEVE_ITEMS, fr.getState());
          assertEquals(2, fr.getPageNumber());
          kSession.dispose();          
          
	}
    
    @Test
	public void testEBayRequestRetieveMoreItemsEvaluate() throws Exception {    	
    	  EBayFindRequest fr = new EBayFindRequest();
          // initial state
    	  fr.setItemsTotal(0);
    	  fr.setItemsMaxRequired(400);
    	  fr.setSearchWords("searchWords_0");  
    	  fr.setItemsTotalInRequest(300);
    	  fr.setPagesTotal(3);
    	  fr.setItemsPerPage(100);
          evaluate.evaluate(fr, EBayRequestService.RULES_EBAY_REQUEST_AGENDA);
          assertEquals(RequestState.RETRIEVE_ITEMS, fr.getState());
          assertEquals(1, fr.getPageNumber());          
          // after processing the first response          
          fr.setItemsTotal(100);
          fr.setItemsUpdatedDuringLastPeriod(100);
          evaluate.evaluate(fr, EBayRequestService.RULES_EBAY_REQUEST_AGENDA);           
          assertEquals(RequestState.RETRIEVE_ITEMS, fr.getState());
          assertEquals(2, fr.getPageNumber());
          fr.setItemsTotal(200);
          fr.setItemsUpdatedDuringLastPeriod(100);
          evaluate.evaluate(fr, EBayRequestService.RULES_EBAY_REQUEST_AGENDA);           
          assertEquals(RequestState.RETRIEVE_ITEMS, fr.getState());
          assertEquals(3, fr.getPageNumber());
          fr.setItemsTotal(300);
          fr.setItemsUpdatedDuringLastPeriod(300);
          evaluate.evaluate(fr, EBayRequestService.RULES_EBAY_REQUEST_AGENDA);           
          assertEquals(RequestState.CHANGE_SEARCH, fr.getState());
          assertEquals(1, fr.getPageNumber());
          fr.setItemsUpdatedDuringLastPeriod(300);
          evaluate.evaluate(fr, EBayRequestService.RULES_EBAY_REQUEST_AGENDA);   
          assertEquals(RequestState.RETRIEVE_ITEMS, fr.getState());
          fr.setItemsUpdatedDuringLastPeriod(400);
          fr.setItemsTotal(400);
          evaluate.evaluate(fr, EBayRequestService.RULES_EBAY_REQUEST_AGENDA);   
          assertEquals(RequestState.WAIT, fr.getState());
	}
}
